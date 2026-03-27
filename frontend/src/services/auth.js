const API_BASE_URL = "http://localhost:8080";

export const loginUser = async (email, password) => {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Fel e-post eller lösenord.");
        throw new Error("Något gick fel vid inloggning.");
    }

    return fetchCurrentUser();
};

export const logoutUser = async () => {
    await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: "POST",
        credentials: "include",
    });
};

export const fetchCurrentUser = async () => {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
        credentials: "include",
    });

    if (!response.ok) return null;
    return await response.json();
};

export const fetchUserSchedule = async (userId) => {
    const response = await fetch(`${API_BASE_URL}/api/schedules/${userId}`, {
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 404) return null;
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        throw new Error("Kunde inte hämta schema.");
    }

    return await response.json();
};

export const fetchTodayTimeEntry = async (userId) => {
    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/today`, {
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 404) return null;
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        throw new Error("Kunde inte hämta dagens tidrapport.");
    }

    return await response.json();
};

export const postTimeAction = async (userId, action) => {
    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/${action}`, {
        method: "POST",
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera tidrapporteringen.");
    }

    return await response.json();
};

export const fetchTimeHistory = async (userId) => {
    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/history`, {
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 404) return [];
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        throw new Error("Kunde inte hämta historik.");
    }

    return await response.json();
};

export const changePassword = async (currentPassword, newPassword) => {
    const response = await fetch(`${API_BASE_URL}/api/users/me/password`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ currentPassword, newPassword }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte byta lösenord.");
    }

    return true;
};

export const fetchFlexBalance = async (userId) => {
    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/flex-balance`, {
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        throw new Error("Kunde inte hämta flexsaldo.");
    }

    return await response.json();
};

export const fetchAllUsers = async () => {
    const response = await fetch(`${API_BASE_URL}/api/admin/users`, {
        credentials: "include",
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att se användare.");
        throw new Error("Kunde inte hämta användarlistan.");
    }

    return await response.json();
};

export const updateMyProfile = async ({ firstName, lastName, email }) => {
    const response = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ firstName, lastName, email }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera profilen.");
    }

    return await response.json();
};

export const createUserAsAdmin = async ({ firstName, lastName, email, password, role, active }) => {
    const response = await fetch(`${API_BASE_URL}/api/admin/users`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ firstName, lastName, email, password, role, active }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att skapa användare.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte skapa användaren.");
    }

    return await response.json();
};

export const updateUserAsAdmin = async (userId, { firstName, lastName, email, role, active }) => {
    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ firstName, lastName, email, role, active }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att redigera användare.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera användaren.");
    }

    return await response.json();
};

export const createManualTimeEntry = async (userId, { workDate, checkInTime, lunchOutTime, lunchInTime, checkOutTime, comment }) => {
    const response = await fetch(`${API_BASE_URL}/api/time/${userId}/manual`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ workDate, checkInTime, lunchOutTime, lunchInTime, checkOutTime, comment }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte spara manuell tidrapport.");
    }

    return await response.json();
};

export const resetUserPasswordAsAdmin = async (userId, newPassword) => {
    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}/password`, {
        method: "PUT",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ newPassword }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att byta lösenord för användare.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte uppdatera lösenordet.");
    }

    return true;
};

export const saveUserScheduleAsAdmin = async (userId, { workDate, plannedStartTime, plannedEndTime, paidLunchMinutes }) => {
    const response = await fetch(`${API_BASE_URL}/api/schedules/${userId}`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ workDate, plannedStartTime, plannedEndTime, paidLunchMinutes }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att spara schema.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte spara schema.");
    }

    return await response.json();
};

export const createRecurringSchedulesAsAdmin = async (userId, { startDate, endDate, plannedStartTime, plannedEndTime, paidLunchMinutes }) => {
    const response = await fetch(`${API_BASE_URL}/api/admin/users/${userId}/schedules/recurring`, {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ startDate, endDate, plannedStartTime, plannedEndTime, paidLunchMinutes }),
    });

    if (!response.ok) {
        if (response.status === 401) throw new Error("Sessionen har gått ut.");
        if (response.status === 403) throw new Error("Du har inte behörighet att skapa återkommande schema.");
        const errorText = await response.text();
        throw new Error(errorText || "Kunde inte skapa återkommande schema.");
    }

    return await response.json();
};