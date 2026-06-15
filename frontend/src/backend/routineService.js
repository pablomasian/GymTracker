import { appFetch, fetchConfig } from "./appFetch";

export const getMyRoutines = (onSuccess, onErrors) => {
    return appFetch("/routines/my-routines", fetchConfig("GET"), onSuccess, onErrors);
};

export const updateRoutine = (id, routineData, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}`, fetchConfig("PUT", routineData), onSuccess, onErrors);
};

export const deleteRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}`, fetchConfig("DELETE"), onSuccess, onErrors);
};

export const publishRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/publish`, fetchConfig("POST"), onSuccess, onErrors);
};

export const hideRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/hide`, fetchConfig("POST"), onSuccess, onErrors);
};

export const getPendingRoutines = (onSuccess, onErrors) => {
    return appFetch("/routines/pending", fetchConfig("GET"), onSuccess, onErrors);
};

export const approveRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/approve`, fetchConfig("PUT"), onSuccess, onErrors);
};

export const dismissRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/dismiss`, fetchConfig("DELETE"), onSuccess, onErrors);
};

export const getAllRoutinesForAdmin = (onSuccess, onErrors) => {
    return appFetch("/routines/all", fetchConfig("GET"), onSuccess, onErrors);
};

export const blockRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/block`, fetchConfig("PUT"), onSuccess, onErrors);
};

export const unblockRoutine = (id, onSuccess, onErrors) => {
    return appFetch(`/routines/${id}/unblock`, fetchConfig("PUT"), onSuccess, onErrors);
};