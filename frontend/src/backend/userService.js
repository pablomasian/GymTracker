import {
  fetchConfig,
  appFetch,
  setServiceToken,
  getServiceToken,
  removeServiceToken,
  setReauthenticationCallback,
} from "./appFetch";
import { config } from "../config/constants";

const processLoginSignUp = (authenticatedUser, reauthenticationCallback, onSuccess) => {
  setServiceToken(authenticatedUser.serviceToken);
  setReauthenticationCallback(reauthenticationCallback);
  if (onSuccess) onSuccess(authenticatedUser);
};

export const login = (username, password, onSuccess, onErrors, reauthenticationCallback) =>
  appFetch(
    "/users/login",
    fetchConfig("POST", { username, password }),
    (authenticatedUser) => {
      processLoginSignUp(authenticatedUser, reauthenticationCallback, onSuccess);
    },
    onErrors
  );

export const tryLoginFromServiceToken = (onSuccess, reauthenticationCallback) => {
  const serviceToken = getServiceToken();
  if (!serviceToken) {
    onSuccess && onSuccess();
    return;
  }

  setReauthenticationCallback(reauthenticationCallback);

  appFetch(
    "/users/loginFromServiceToken",
    fetchConfig("POST"),
    (authenticatedUser) => onSuccess(authenticatedUser),
    () => removeServiceToken()
  );
};

export const signUp = (user, onSuccess, onErrors, reauthenticationCallback) =>
  appFetch(
    "/users/signUp",
    fetchConfig("POST", user),
    (authenticatedUser) => {
      processLoginSignUp(authenticatedUser, reauthenticationCallback, onSuccess);
    },
    onErrors
  );

export const logout = () => removeServiceToken();

// Actualiza datos públicos (nombre, email)
export const updateProfile = (user, onSuccess, onErrors) =>
  appFetch(`/users/${user.id}/public`, fetchConfig("PUT", user), onSuccess, onErrors);

// Subir avatar
export const uploadAvatar = (username, file, onSuccess, onErrors) => {
  const fd = new FormData();
  fd.append("file", file);
  return appFetch(`/users/${username}/avatar`, fetchConfig("POST", fd), onSuccess, onErrors);
};

// Cambiar contraseña
export const changePassword = (id, oldPassword, newPassword, onSuccess, onErrors) =>
  appFetch(
    `/users/${id}/changePassword`,
    fetchConfig("POST", { oldPassword, newPassword }),
    onSuccess,
    onErrors
  );

// Admin
export const listAll = () =>
  new Promise((resolve, reject) => {
    appFetch("/users", fetchConfig("GET"), resolve, reject);
  });

export const block = (userId) =>
  new Promise((resolve, reject) => {
    appFetch(`/users/${userId}/block`, fetchConfig("PUT"), resolve, reject);
  });

export const unblock = (userId) =>
  new Promise((resolve, reject) => {
    appFetch(`/users/${userId}/unblock`, fetchConfig("PUT"), resolve, reject);
  });

// Perfil privado y datos de fitness
export function getPrivateProfile(userId, onSuccess, onError) {
  appFetch(`/users/${userId}/private`, fetchConfig("GET"), onSuccess, onError);
}

// **Corregido**: actualizar altura, peso y edad usando el endpoint correcto
export function updateFitnessData(userId, data, onSuccess, onError) {
  const payload = {
    height: data.altura ? Number(data.altura) : undefined,
    weight: data.peso ? Number(data.peso) : undefined,
    age: data.edad ? Number(data.edad) : undefined,
  };
  appFetch(`/users/${userId}`, fetchConfig("PUT", payload), onSuccess, onError);
}

export const searchUsers = (query, onSuccess, onErrors) =>
  appFetch(
    `/users/search?query=${encodeURIComponent(query)}`,
    fetchConfig("GET"),
    onSuccess,
    onErrors
  );