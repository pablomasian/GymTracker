import * as routineService from '../backend/routineService';
import * as userService from '../backend/userService';
import workoutService from '../backend/workoutService';
import * as appFetchModule from '../backend/appFetch';

jest.mock('../backend/appFetch', () => ({
  __esModule: true, 
  appFetch: jest.fn(),
  fetchConfig: jest.fn(),
  setServiceToken: jest.fn(),
  getServiceToken: jest.fn(),
  removeServiceToken: jest.fn(),
  setReauthenticationCallback: jest.fn(),
}));

describe('Service Functions', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    appFetchModule.fetchConfig.mockReturnValue({ headers: { 'Content-Type': 'application/json' } });
  });

  test('routineService: deleteRoutine calls DELETE', () => {
    routineService.deleteRoutine(1, jest.fn(), jest.fn());
    expect(appFetchModule.fetchConfig).toHaveBeenCalledWith('DELETE');
    expect(appFetchModule.appFetch).toHaveBeenCalledWith('/routines/1', expect.anything(), expect.any(Function), expect.any(Function));
  });

  test('routineService: publishRoutine calls POST', () => {
    routineService.publishRoutine(1, jest.fn(), jest.fn());
    expect(appFetchModule.fetchConfig).toHaveBeenCalledWith('POST');
    expect(appFetchModule.appFetch).toHaveBeenCalledWith('/routines/1/publish', expect.anything(), expect.any(Function), expect.any(Function));
  });

  test('userService: updateProfile calls PUT', () => {
    const user = { id: 1, name: 'Test' };
    userService.updateProfile(user, jest.fn(), jest.fn());
    expect(appFetchModule.fetchConfig).toHaveBeenCalledWith('PUT', user);
    expect(appFetchModule.appFetch).toHaveBeenCalledWith('/users/1/public', expect.anything(), expect.any(Function), expect.any(Function));
  });

  test('userService: searchUsers calls GET with query', () => {
    userService.searchUsers('john', jest.fn(), jest.fn());
    expect(appFetchModule.appFetch).toHaveBeenCalledWith(expect.stringContaining('/users/search?query=john'), expect.anything(), expect.any(Function), expect.any(Function));
  });

  test('workoutService: startWorkout calls POST', () => {
    workoutService.startWorkout(10, jest.fn(), jest.fn());
    expect(appFetchModule.appFetch).toHaveBeenCalledWith('/workouts/start/10', expect.anything(), expect.any(Function), expect.any(Function));
  });

  test('workoutService: finishWorkout calls POST', () => {
    workoutService.finishWorkout(55, { duration: 60 }, jest.fn(), jest.fn());
    expect(appFetchModule.fetchConfig).toHaveBeenCalledWith('POST', { duration: 60 });
    expect(appFetchModule.appFetch).toHaveBeenCalledWith('/workouts/finish/55', expect.anything(), expect.any(Function), expect.any(Function));
  });
});