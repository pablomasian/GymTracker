
export const config = {
  // En producción usa URL relativa, en desarrollo usa localhost
  BASE_PATH: process.env.NODE_ENV === 'production' 
    ? "/gymtracker/api" 
    : "http://localhost:8080/gymtracker/api",
  SERVICE_TOKEN_NAME: "serviceToken"
};

