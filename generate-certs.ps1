# ===============================
# HTTPS CERTIFICATE GENERATOR
# Root CA + Frontend + Backend
# Works on Windows (PowerShell)
# ===============================

$ErrorActionPreference = "Stop"

# === Settings ===
$STOREPASS = "changeit"
$VALIDITY_CA = 3650
$VALIDITY_SRV = 825
$ROOT_NAME = "My Dev Root CA"
$ORG = "MyOrg"
$COUNTRY = "RU"
$FRONT_HOST = "frontend.local"
$BACK_HOST = "backend.local"

# === Folder structure ===
New-Item -ItemType Directory -Force -Path certs, certs\ca, certs\frontend, certs\backend | Out-Null

Write-Host "=== 1. Generating ROOT CA ==="
keytool -genkeypair -alias myca -keyalg RSA -keysize 4096 -validity $VALIDITY_CA `
  -storetype PKCS12 -keystore certs\ca\ca.p12 -storepass $STOREPASS -keypass $STOREPASS `
  -dname "CN=$ROOT_NAME, O=$ORG, C=$COUNTRY" `
  -ext bc=ca:true -ext ku=keyCertSign,cRLSign

keytool -exportcert -rfc -alias myca `
  -keystore certs\ca\ca.p12 -storepass $STOREPASS `
  -file certs\ca\ca.crt

Write-Host "`n✅ Root CA created at: certs\ca\ca.crt"
Write-Host "Install this certificate into Windows Trusted Root Certification Authorities.`n"

# === Function to issue service certs ===
function New-ServiceCert {
    param($hostName, $alias, $folder)

    Write-Host "=== Generating cert for $hostName ==="

    keytool -genkeypair -alias $alias -keyalg RSA -keysize 2048 -validity $VALIDITY_SRV `
      -storetype PKCS12 -keystore $folder\$alias.p12 `
      -storepass $STOREPASS -keypass $STOREPASS `
      -dname "CN=$hostName, O=$ORG, C=$COUNTRY"

    keytool -certreq -alias $alias `
      -keystore $folder\$alias.p12 -storepass $STOREPASS `
      -file $folder\$alias.csr

    keytool -gencert -alias myca `
      -keystore certs\ca\ca.p12 -storepass $STOREPASS `
      -infile $folder\$alias.csr `
      -outfile $folder\$alias.crt `
      -rfc -validity $VALIDITY_SRV `
      -ext san=dns:$hostName `
      -ext eku=serverAuth `
      -ext ku=digitalSignature,keyEncipherment

    keytool -importcert -noprompt -alias myca `
      -file certs\ca\ca.crt `
      -keystore $folder\$alias.p12 -storepass $STOREPASS

    keytool -importcert -alias $alias `
      -file $folder\$alias.crt `
      -keystore $folder\$alias.p12 -storepass $STOREPASS

    # Export key and cert for Vite if frontend
    if ($alias -eq "frontend") {
        openssl pkcs12 -in $folder\$alias.p12 -nokeys -out $folder\$alias.crt -passin pass:$STOREPASS
        openssl pkcs12 -in $folder\$alias.p12 -nocerts -out $folder\$alias.key -passin pass:$STOREPASS -passout pass:$STOREPASS
        openssl rsa -in $folder\$alias.key -out $folder\$alias.key -passin pass:$STOREPASS
    }

    Write-Host "✅ $alias certificate created."
}

# === 2. Backend ===
New-ServiceCert -hostName $BACK_HOST -alias "backend" -folder "certs\backend"

# === 3. Frontend ===
New-ServiceCert -hostName $FRONT_HOST -alias "frontend" -folder "certs\frontend"

Write-Host "`n✅ All certificates generated successfully!"
Write-Host "Certificates are located in ./certs/"
Write-Host "Add these lines to your hosts file:"
Write-Host "127.0.0.1 $FRONT_HOST"
Write-Host "127.0.0.1 $BACK_HOST"
