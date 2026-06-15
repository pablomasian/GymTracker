import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext(null);
export const useAuth = () => useContext(AuthContext);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  // Login real (usa backend/userService.login)
  async function login(username, password) {
    return new Promise((resolve, reject) => {
      import("../backend/userService").then(({ login }) => {
        login(
          username,
          password,
          (authenticated) => {
            // Normaliza: puede venir como { user: {...} } o { userDto: {...} }
            const normalizedUser = authenticated?.user ?? authenticated?.userDto ?? authenticated;
            setUser(normalizedUser);
            // Devolvemos un shape homogéneo para quien lo use
            resolve({ userDto: normalizedUser, serviceToken: authenticated?.serviceToken });
          },
          (error) => reject(error)
        );
      });
    });
  }

  function logout() {
    setUser(null);
    // opcional: si usas storage de token, aquí podrías limpiarlo
    // removeServiceToken?.();
  }

  async function register({ name = "", username, password, coach, premium, avatarFile }) {
  // Derivar nombres igual que antes
  const parts = name.trim().split(/\s+/).filter(Boolean);
  const firstName = parts.length ? parts[0] : "-";
  const lastName = parts.length > 1 ? parts.slice(1).join(" ") : "-";

  return new Promise((resolve, reject) => {
    import("../backend/userService").then(({ signUp }) => {
      signUp(
        {
          nombreUsuario: name || username,
          firstName,
          lastName,
          username,
          password,
          role: coach ? "COACH" : "USER",
          premium: coach ? (premium || false) : undefined, // Solo enviar premium si es coach
        },
        async (registered) => { // <- esta función debe ser async
          const normalizedUser = registered?.user ?? registered?.userDto ?? registered;
          setUser(normalizedUser);

          // 🔹 Si hay imagen, súbela al backend
          if (normalizedUser && avatarFile) {
            const formData = new FormData();
            formData.append("file", avatarFile);

            try {
              await fetch(`/api/users/${normalizedUser.username}/avatar`, {
                method: "POST",
                body: formData,
              });
            } catch (err) {
              console.error("Error al subir avatar:", err);
            }
          }

          resolve({ userDto: normalizedUser, serviceToken: registered?.serviceToken });
        },
        (error) => reject(error)
      );
    });
  });
}


  // Rehidratación al montar (si hay token en sessionStorage)
  useEffect(() => {
    let cancelled = false;
    import("../backend/userService").then(({ tryLoginFromServiceToken }) => {
      tryLoginFromServiceToken(
        (authenticated) => {
          if (cancelled) return;
            const normalized = authenticated?.user ?? authenticated?.userDto ?? authenticated;
            if (normalized) setUser(normalized);
            setAuthLoading(false);
        },
        () => { if(!cancelled) setAuthLoading(false); }
      );
    });
    return () => { cancelled = true; };
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, register, authLoading, updateUser: setUser }}>
      {children}
    </AuthContext.Provider>
  );
}
