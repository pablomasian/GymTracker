import { init } from "./appFetch";
import * as userService from "./userService";

export { default as NetworkError } from "./NetworkError";
export { userService };

export default { init, userService };
