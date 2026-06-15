jest.mock('../backend/appFetch', () => ({
  fetchConfig: jest.fn(),
  appFetch: jest.fn(),
  setServiceToken: jest.fn(),
  getServiceToken: jest.fn(),
  removeServiceToken: jest.fn(),
  setReauthenticationCallback: jest.fn(),
}));

import { appFetch, fetchConfig, setServiceToken, getServiceToken, removeServiceToken, setReauthenticationCallback } from '../backend/appFetch';
import * as userService from '../backend/userService';

describe('userService', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('login calls appFetch and then sets service token', () => {
    const fakeAuth = { serviceToken: 'tok-1', user: { id: 1 } };
    // simulate appFetch calling success callback
    appFetch.mockImplementation((_path, _cfg, onSuccess) => onSuccess(fakeAuth));
    const onSuccess = jest.fn();
    const onErrors = jest.fn();
    userService.login('u', 'p', onSuccess, onErrors, () => {});
    expect(fetchConfig).toHaveBeenCalledWith('POST', { username: 'u', password: 'p' });
    expect(setServiceToken).toHaveBeenCalledWith('tok-1');
    expect(onSuccess).toHaveBeenCalled();
  });

  test('tryLoginFromServiceToken when no token calls onSuccess immediately', () => {
    getServiceToken.mockReturnValue(null);
    const onSuccess = jest.fn();
    userService.tryLoginFromServiceToken(onSuccess, () => {});
    expect(onSuccess).toHaveBeenCalled();
  });

  test('tryLoginFromServiceToken with token sets callback and calls appFetch', () => {
    getServiceToken.mockReturnValue('tok');
    // ensure fetchConfig returns a config object so appFetch is called with it
    fetchConfig.mockReturnValue({});
    const reauth = jest.fn();
    appFetch.mockImplementation((_p, _c, onSuccess) => onSuccess({ serviceToken: 'tok' }));
    const onSuccess = jest.fn();
    userService.tryLoginFromServiceToken(onSuccess, reauth);
    expect(setReauthenticationCallback).toHaveBeenCalledWith(reauth);
    expect(appFetch).toHaveBeenCalledWith('/users/loginFromServiceToken', expect.any(Object), expect.any(Function), expect.any(Function));
  });
});
