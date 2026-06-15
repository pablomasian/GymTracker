import { appFetch, fetchConfig, init, setReauthenticationCallback, setServiceToken, getServiceToken, removeServiceToken } from '../backend/appFetch';
import NetworkError from '../backend/NetworkError';

describe('appFetch utilities', () => {
  afterEach(() => {
    jest.restoreAllMocks();
    sessionStorage.clear();
  });

  test('fetchConfig sets JSON headers and Authorization when token present', () => {
    // `SERVICE_TOKEN_NAME` in config is 'serviceToken'
    sessionStorage.setItem('serviceToken', 'tok-123');
    const cfg = fetchConfig('POST', { a: 1 });
    expect(cfg.method).toBe('POST');
    expect(cfg.headers['Content-Type']).toBe('application/json');
    expect(cfg.headers['Authorization']).toBe('Bearer tok-123');
    expect(typeof cfg.body).toBe('string');
  });

  test('appFetch handles 204 no-content', async () => {
    global.fetch = jest.fn().mockResolvedValue({ ok: true, status: 204, headers: { get: () => null } });
    const onSuccess = jest.fn();
    await appFetch('/test', fetchConfig('GET'), onSuccess);
    expect(onSuccess).toHaveBeenCalledTimes(1);
  });

  test('appFetch handles JSON responses', async () => {
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      headers: { get: () => 'application/json' },
      json: () => Promise.resolve({ hello: 'world' }),
    });
    const onSuccess = jest.fn();
    await appFetch('/j', fetchConfig('GET'), onSuccess);
    // json handler is async inside handler; wait a tick
    await new Promise((r) => setTimeout(r, 0));
    expect(onSuccess).toHaveBeenCalledWith({ hello: 'world' });
  });

  test('appFetch handles blob responses', async () => {
    const fakeBlob = { size: 10 };
    global.fetch = jest.fn().mockResolvedValue({
      ok: true,
      status: 200,
      headers: { get: () => 'application/octet-stream' },
      blob: () => Promise.resolve(fakeBlob),
    });
    const onSuccess = jest.fn();
    await appFetch('/b', fetchConfig('GET'), onSuccess);
    await new Promise((r) => setTimeout(r, 0));
    expect(onSuccess).toHaveBeenCalledWith(fakeBlob);
  });

  test('401 triggers reauthentication callback', async () => {
    const reac = jest.fn();
    setReauthenticationCallback(reac);
    global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 401, headers: { get: () => 'application/json' } });
    const onErrors = jest.fn();
    await appFetch('/x', fetchConfig('GET'), jest.fn(), onErrors);
    expect(reac).toHaveBeenCalled();
  });

  test('4xx with json calls onErrors with payload', async () => {
    const payload = { globalError: 'oops' };
    global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 400, headers: { get: () => 'application/json' }, json: () => Promise.resolve(payload) });
    const onErrors = jest.fn();
    await appFetch('/err', fetchConfig('GET'), jest.fn(), onErrors);
    // wait for async json->onErrors
    await new Promise((r) => setTimeout(r, 0));
    expect(onErrors).toHaveBeenCalledWith(payload);
  });

  test('non-4xx non-ok triggers networkErrorCallback via init', async () => {
    const netcb = jest.fn();
    init(netcb);
    global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 500, headers: { get: () => null } });
    await appFetch('/bad', fetchConfig('GET'), jest.fn(), jest.fn());
    expect(netcb).toHaveBeenCalled();
  });
});
