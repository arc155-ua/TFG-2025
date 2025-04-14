# Descargar Bootstrap CSS
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" -OutFile "src/main/resources/static/css/bootstrap.min.css"
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap-reboot.min.css" -OutFile "src/main/resources/static/css/bootstrap-reboot.min.css"
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap-grid.min.css" -OutFile "src/main/resources/static/css/bootstrap-grid.min.css"

# Descargar Bootstrap JS
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" -OutFile "src/main/resources/static/js/bootstrap.min.js"

# Descargar jQuery
Invoke-WebRequest -Uri "https://code.jquery.com/jquery-3.6.0.min.js" -OutFile "src/main/resources/static/js/jquery.min.js"

# Descargar Popper
Invoke-WebRequest -Uri "https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js" -OutFile "src/main/resources/static/js/popper.min.js" 