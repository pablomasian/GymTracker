jest.mock('../backend/appFetch', () => ({
  appFetch: jest.fn(),
  fetchConfig: jest.fn(),
}));

import { appFetch, fetchConfig } from '../backend/appFetch';
import * as routineService from '../backend/routineService';

describe('routineService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('getMyRoutines calls appFetch with correct path and config', () => {
    const cfg = { method: 'GET' };
    fetchConfig.mockReturnValue(cfg);
    const onSuccess = jest.fn();
    const onErrors = jest.fn();
    routineService.getMyRoutines(onSuccess, onErrors);
    expect(fetchConfig).toHaveBeenCalledWith('GET');
    expect(appFetch).toHaveBeenCalledWith('/routines/my-routines', cfg, onSuccess, onErrors);
  });

  test('updateRoutine builds correct path and uses PUT', () => {
    const cfg = { method: 'PUT' };
    fetchConfig.mockReturnValue(cfg);
    const onSuccess = jest.fn();
    const onErrors = jest.fn();
    routineService.updateRoutine(42, { name: 'x' }, onSuccess, onErrors);
    expect(fetchConfig).toHaveBeenCalledWith('PUT', { name: 'x' });
    expect(appFetch).toHaveBeenCalledWith('/routines/42', cfg, onSuccess, onErrors);
  });
});
