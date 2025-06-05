class BarcodeScanner {
    constructor() {
        this.video = document.getElementById('video');
        this.status = document.getElementById('status');
        this.stream = null;
        this.scanning = false;
        this.init();
    }

    updateStatus(message) {
        this.status.textContent = message;
    }

    async startScanner() {
        this.updateStatus('Iniciando cámara...');
        
        try {
            // Obtener acceso a la cámara
            this.stream = await navigator.mediaDevices.getUserMedia({
                video: {
                    facingMode: 'environment',
                    width: { ideal: 1280 },
                    height: { ideal: 720 }
                }
            });

            // Mostrar el video
            this.video.srcObject = this.stream;
            await this.video.play();
            
            // Iniciar el escaneo
            this.scanning = true;
            this.scanCode();
            this.updateStatus('Escaneando...');
        } catch (err) {
            console.error('Error:', err);
            this.updateStatus('Error: ' + err.message);
            document.querySelector('.error').innerHTML = `
                <strong>${err.name}</strong><br>${err.message}<br>
                ¿Está permitido el acceso a la cámara?<br>
                ¿Estás accediendo desde HTTPS o localhost?
            `;
        }
    }

    async scanCode() {
        if (!this.scanning) return;

        try {
            // Crear un canvas temporal para capturar el frame
            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            canvas.width = this.video.videoWidth;
            canvas.height = this.video.videoHeight;
            context.drawImage(this.video, 0, 0, canvas.width, canvas.height);

            // Enviar el frame al servidor para procesamiento
            const response = await fetch('/scanner/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    imageData: canvas.toDataURL('image/jpeg')
                })
            });

            if (response.ok) {
                const result = await response.json();
                if (result.code) {
                    this.updateStatus('Código detectado: ' + result.code);
                    this.stopScanner();
                    window.location.href = `/food/barcode/${result.code}`;
                }
            }
        } catch (err) {
            console.error('Error al escanear:', err);
        }
        
        if (this.scanning) {
            requestAnimationFrame(() => this.scanCode());
        }
    }

    stopScanner() {
        this.scanning = false;
        if (this.stream) {
            this.stream.getTracks().forEach(track => track.stop());
            this.stream = null;
        }
        this.updateStatus('Escáner detenido');
    }

    init() {
        // Iniciar el escáner cuando se carga la página
        this.startScanner();

        // Detener el escáner cuando se cierra la página
        window.addEventListener('beforeunload', () => this.stopScanner());
    }
}

// Inicializar el escáner cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new BarcodeScanner();
}); 