import { appFetch, fetchConfig } from "./appFetch";

const feedService = {
  getUserFeed: (onSuccess, onErrors) =>
    appFetch("/feed", fetchConfig("GET"), onSuccess, onErrors),
};

export default feedService;
