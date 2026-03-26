const API_BASE_URL = "http://localhost:8080";

export const createBasicAuthHeader = (email, password) => {
    return "Basic " + btoa(`${email}:${password}`);
};

export const saveAuthHeader = (authHeader) => {
    localStorage.setItem("authHeader", authHeader);
};

export const getAuthHeader = () => {
    return localStorage.getItem("authHeader");
};

export const clearAuthHeader = () => {
    localStorage.removeItem("authHeader");
};

export const fetchCurrentUser = async () => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        return null;
    }

    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        clearAuthHeader();
        return null;
    }

    return await response.json();
};

export const loginUser = async (email, password) => {
    const authHeader = createBasicAuthHeader(email, password);

    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 401) {
            throw new Error("Fel e-post eller lösenord.");
        }
        throw new Error("Något gick fel vid inloggning.");
    }

    saveAuthHeader(authHeader);
    return await response.json();
};

export const fetchUserSchedule = async (userId) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/schedules/${userId}`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 404) {
            return null;
        }

        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        throw new Error("Kunde inte hämta schema.");
    }

    return await response.json();
};

export const fetchTodayTimeEntry = async (userId) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/today`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 404) {
            return null;
        }

        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        throw new Error("Kunde inte hämta dagens tidrapport.");
    }

    return await response.json();
};

export const postTimeAction = async (userId, action) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/${action}`, {
        method: "POST",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera tidrapporteringen.");
    }

    return await response.json();
};

export const fetchTimeHistory = async (userId) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/history`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 404) {
            return [];
        }

        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        throw new Error("Kunde inte hämta historik.");
    }

    return await response.json();
};

export const changePassword = async (currentPassword, newPassword) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/users/me/password`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            currentPassword,
            newPassword,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte byta lösenord.");
    }

    return true;
};

export const fetchFlexBalance = async (userId) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/flex-balance`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        throw new Error("Kunde inte hämta flexsaldo.");
    }

    return await response.json();
};

export const fetchAllUsers = async () => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/admin/users`, {
        method: "GET",
        headers: {
            Authorization: authHeader,
        },
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        if (response.status === 403) {
            throw new Error("Du har inte behörighet att se användare.");
        }

        throw new Error("Kunde inte hämta användarlistan.");
    }

    return await response.json();
};

export const updateMyProfile = async ({ firstName, lastName, email }) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            firstName,
            lastName,
            email,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera profilen.");
    }

    return await response.json();
};

export const createUserAsAdmin = async ({
                                            firstName,
                                            lastName,
                                            email,
                                            password,
                                            role,
                                            active,
                                        }) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/admin/users`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            firstName,
            lastName,
            email,
            password,
            role,
            active,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        if (response.status === 403) {
            throw new Error("Du har inte behörighet att skapa användare.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte skapa användaren.");
    }

    return await response.json();
};

export const updateUserAsAdmin = async (
    userId,
    { firstName, lastName, email, role, active }
) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            firstName,
            lastName,
            email,
            role,
            active,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        if (response.status === 403) {
            throw new Error("Du har inte behörighet att redigera användare.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera användaren.");
    }

    return await response.json();
};

export const createManualTimeEntry = async (
    userId,
    { workDate, checkInTime, lunchOutTime, lunchInTime, checkOutTime, comment }
) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/manual`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            workDate,
            checkInTime,
            lunchOutTime,
            lunchInTime,
            checkOutTime,
            comment,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte spara manuell tidrapport.");
    }

    return await response.json();
};

export const resetUserPasswordAsAdmin = async (userId, newPassword) => {
    const authHeader = getAuthHeader();

    if (!authHeader) {
        throw new Error("Ingen aktiv session.");
    }

    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}/password`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: authHeader,
        },
        body: JSON.stringify({
            newPassword,
        }),
    });

    if (!response.ok) {
        if (response.status === 401) {
            clearAuthHeader();
            throw new Error("Sessionen har gått ut.");
        }

        if (response.status === 403) {
            throw new Error("Du har inte behörighet att byta lösenord för användare.");
        }

        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera lösenordet.");
    }

    return true;
};