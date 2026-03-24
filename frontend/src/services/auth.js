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