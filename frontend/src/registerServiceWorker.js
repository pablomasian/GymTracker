// En producción, se registra un service worker para servir assets desde caché local.

// Esto hace que la app cargue más rápido en visitas posteriores y permite modo offline.
// Ojo: los usuarios pueden ver actualizaciones en la siguiente visita, ya que la caché se actualiza en segundo plano.

// Más info: https://goo.gl/KwvDNy (incluye cómo desactivar este comportamiento).

const isLocalhost = Boolean(
  window.location.hostname === 'localhost' ||
  // [::1] es la dirección de localhost en IPv6.
    window.location.hostname === '[::1]' ||
  // 127.0.0.1/8 se considera localhost para IPv4.
    window.location.hostname.match(
      /^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/
    )
);

export default function register() {
  if (process.env.NODE_ENV === 'production' && 'serviceWorker' in navigator) {
  // El constructor URL está disponible en navegadores con soporte SW.
    const publicUrl = new URL(process.env.PUBLIC_URL, window.location);
    if (publicUrl.origin !== window.location.origin) {
  // El SW no funcionará si PUBLIC_URL está en otro origen distinto al de la página (p.ej., CDN)
  // https://github.com/facebookincubator/create-react-app/issues/2374
      return;
    }

    window.addEventListener('load', () => {
      const swUrl = `${process.env.PUBLIC_URL}/service-worker.js`;

      if (isLocalhost) {
  // Ejecutando en localhost: comprobamos si existe SW registrado
        checkValidServiceWorker(swUrl);

  // Log extra en localhost con documentación de PWA
        navigator.serviceWorker.ready.then(() => {
          console.log(
            'This web app is being served cache-first by a service ' +
              'worker. To learn more, visit https://goo.gl/SC7cgQ'
          );
        });
      } else {
  // No es localhost: registrar el SW
        registerValidSW(swUrl);
      }
    });
  }
}

function registerValidSW(swUrl) {
  navigator.serviceWorker
    .register(swUrl)
    .then(registration => {
      registration.onupdatefound = () => {
        const installingWorker = registration.installing;
        installingWorker.onstatechange = () => {
          if (installingWorker.state === 'installed') {
            if (navigator.serviceWorker.controller) {
              // El contenido nuevo ya está en caché; avisar al usuario para recargar.
              console.log('New content is available; please refresh.');
            } else {
              // Todo ha sido precacheado: indicar que hay contenido offline disponible.
              console.log('Content is cached for offline use.');
            }
          }
        };
      };
    })
    .catch(error => {
      console.error('Error during service worker registration:', error);
    });
}

function checkValidServiceWorker(swUrl) {
  // Comprueba si existe el SW; si no, recarga la página
  fetch(swUrl)
    .then(response => {
  // Asegura que el SW existe y que devuelve un JS
      if (
        response.status === 404 ||
        response.headers.get('content-type').indexOf('javascript') === -1
      ) {
  // No se encontró SW (probablemente otra app). Recargar la página.
        navigator.serviceWorker.ready.then(registration => {
          registration.unregister().then(() => {
            window.location.reload();
          });
        });
      } else {
  // SW encontrado: continuar normal
        registerValidSW(swUrl);
      }
    })
    .catch(() => {
      console.log(
        'No internet connection found. App is running in offline mode.'
      );
    });
}

export function unregister() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.ready.then(registration => {
      registration.unregister();
    });
  }
}
