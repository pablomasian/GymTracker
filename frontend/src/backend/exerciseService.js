// src/backend/exerciseService.js
import { appFetch, fetchConfig } from "./appFetch";

export const createExercise = (exercise, onSuccess, onErrors) => {
  appFetch("/exercises", fetchConfig("POST", exercise), onSuccess, onErrors);
};

export const exerciseService = {
  getPending: (onSuccess, onErrors) =>
    appFetch("/exercises/pending", fetchConfig("GET"), onSuccess, onErrors),

  accept: (id, onSuccess, onErrors) =>
    appFetch(`/exercises/${id}/accept`, fetchConfig("PUT"), onSuccess, onErrors),

  dismiss: (id, onSuccess, onErrors) =>
    appFetch(`/exercises/${id}/dismiss`, fetchConfig("DELETE"), onSuccess, onErrors),

  block: (id, onSuccess, onErrors) =>
    appFetch(`/exercises/${id}/block`, fetchConfig("PUT"), onSuccess, onErrors),
};
