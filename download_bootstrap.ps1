# Create directories
New-Item -ItemType Directory -Force -Path "bootstrap/css"
New-Item -ItemType Directory -Force -Path "bootstrap/js"
New-Item -ItemType Directory -Force -Path "css"
New-Item -ItemType Directory -Force -Path "js"
New-Item -ItemType Directory -Force -Path "assets"

# Download Bootstrap CSS
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" -OutFile "bootstrap/css/bootstrap.min.css"

# Download Bootstrap JS
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" -OutFile "bootstrap/js/bootstrap.bundle.min.js" 