import { useEffect, useMemo, useState } from "react";
import {
    fetchAllUsers,
    createUserAsAdmin,
    updateUserAsAdmin,
    createManualTimeEntry,
    resetUserPasswordAsAdmin,
} from "../services/auth";

function AdminPage() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("USER");
    const [active, setActive] = useState(true);

    const [createLoading, setCreateLoading] = useState(false);
    const [createError, setCreateError] = useState("");
    const [createSuccess, setCreateSuccess] = useState("");

    const [selectedUserId, setSelectedUserId] = useState(null);
    const [editFirstName, setEditFirstName] = useState("");
    const [editLastName, setEditLastName] = useState("");
    const [editEmail, setEditEmail] = useState("");
    const [editRole, setEditRole] = useState("USER");
    const [editActive, setEditActive] = useState(true);

    const [editLoading, setEditLoading] = useState(false);
    const [editError, setEditError] = useState("");
    const [editSuccess, setEditSuccess] = useState("");

    const [resetPasswordValue, setResetPasswordValue] = useState("");
    const [resetPasswordLoading, setResetPasswordLoading] = useState(false);
    const [resetPasswordError, setResetPasswordError] = useState("");
    const [resetPasswordSuccess, setResetPasswordSuccess] = useState("");

    const [manualUserId, setManualUserId] = useState("");
    const [manualWorkDate, setManualWorkDate] = useState("");
    const [manualCheckIn, setManualCheckIn] = useState("");
    const [manualLunchOut, setManualLunchOut] = useState("");
    const [manualLunchIn, setManualLunchIn] = useState("");
    const [manualCheckOut, setManualCheckOut] = useState("");
    const [manualComment, setManualComment] = useState("");

    const [manualLoading, setManualLoading] = useState(false);
    const [manualError, setManualError] = useState("");
    const [manualSuccess, setManualSuccess] = useState("");

    const loadUsers = async () => {
        try {
            setLoading(true);
            setError("");

            const data = await fetchAllUsers();
            setUsers(Array.isArray(data) ? data : []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadUsers();
    }, []);

    const stats = useMemo(() => {
        const totalUsers = users.length;
        const adminCount = users.filter((user) => user.role === "ADMIN").length;
        const regularUserCount = users.filter((user) => user.role === "USER").length;
        const activeCount = users.filter((user) => user.active).length;

        return {
            totalUsers,
            adminCount,
            regularUserCount,
            activeCount,
        };
    }, [users]);

    const handleCreateUser = async (e) => {
        e.preventDefault();
        setCreateError("");
        setCreateSuccess("");

        if (password.length < 6) {
            setCreateError("Lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            setCreateLoading(true);

            const createdUser = await createUserAsAdmin({
                firstName,
                lastName,
                email,
                password,
                role,
                active,
            });

            setUsers((prevUsers) => [createdUser, ...prevUsers]);

            setFirstName("");
            setLastName("");
            setEmail("");
            setPassword("");
            setRole("USER");
            setActive(true);

            setCreateSuccess("Användaren har skapats.");
        } catch (err) {
            setCreateError(err.message);
        } finally {
            setCreateLoading(false);
        }
    };

    const handleSelectUser = (user) => {
        setSelectedUserId(user.id);
        setEditFirstName(user.firstName || "");
        setEditLastName(user.lastName || "");
        setEditEmail(user.email || "");
        setEditRole(user.role || "USER");
        setEditActive(!!user.active);

        setResetPasswordValue("");

        setEditError("");
        setEditSuccess("");
        setResetPasswordError("");
        setResetPasswordSuccess("");
    };

    const handleUpdateUser = async (e) => {
        e.preventDefault();

        if (!selectedUserId) {
            return;
        }

        setEditError("");
        setEditSuccess("");

        try {
            setEditLoading(true);

            const updatedUser = await updateUserAsAdmin(selectedUserId, {
                firstName: editFirstName,
                lastName: editLastName,
                email: editEmail,
                role: editRole,
                active: editActive,
            });

            setUsers((prevUsers) =>
                prevUsers.map((user) =>
                    user.id === selectedUserId ? updatedUser : user
                )
            );

            setEditSuccess("Användaren har uppdaterats.");
        } catch (err) {
            setEditError(err.message);
        } finally {
            setEditLoading(false);
        }
    };

    const handleResetPassword = async (e) => {
        e.preventDefault();

        if (!selectedUserId) {
            return;
        }

        setResetPasswordError("");
        setResetPasswordSuccess("");

        if (resetPasswordValue.length < 6) {
            setResetPasswordError("Lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            setResetPasswordLoading(true);

            await resetUserPasswordAsAdmin(selectedUserId, resetPasswordValue);

            setResetPasswordValue("");
            setResetPasswordSuccess("Lösenordet har uppdaterats för användaren.");
        } catch (err) {
            setResetPasswordError(err.message);
        } finally {
            setResetPasswordLoading(false);
        }
    };

    const combineDateAndTime = (date, time) => {
        if (!date || !time) return null;
        return `${date}T${time}:00`;
    };

    const handleManualTimeForUser = async (e) => {
        e.preventDefault();
        setManualError("");
        setManualSuccess("");

        if (!manualUserId || !manualWorkDate || !manualCheckIn || !manualCheckOut) {
            setManualError("Användare, datum, check-in och check-out måste fyllas i.");
            return;
        }

        try {
            setManualLoading(true);

            await createManualTimeEntry(Number(manualUserId), {
                workDate: manualWorkDate,
                checkInTime: combineDateAndTime(manualWorkDate, manualCheckIn),
                lunchOutTime: combineDateAndTime(manualWorkDate, manualLunchOut),
                lunchInTime: combineDateAndTime(manualWorkDate, manualLunchIn),
                checkOutTime: combineDateAndTime(manualWorkDate, manualCheckOut),
                comment: manualComment,
            });

            setManualUserId("");
            setManualWorkDate("");
            setManualCheckIn("");
            setManualLunchOut("");
            setManualLunchIn("");
            setManualCheckOut("");
            setManualComment("");

            setManualSuccess("Manuell tidrapport har registrerats för användaren.");
        } catch (err) {
            setManualError(err.message);
        } finally {
            setManualLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="app-card">
                <h1>Admin</h1>
                <p className="app-card-subtitle">Laddar användare...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="app-card">
                <h1>Admin</h1>
                <p className="app-message-error">{error}</p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Admin</h1>
                <p className="app-card-subtitle">
                    Här kan du hantera användare, uppdatera konton, registrera tid och återställa lösenord.
                </p>
            </section>

            <section className="app-grid">
                <div className="app-card">
                    <h2>Översikt</h2>
                    <div className="app-grid">
                        <div className="app-info-box">
                            <span className="app-label">Totalt antal användare</span>
                            <span className="app-value">{stats.totalUsers}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Admins</span>
                            <span className="app-value">{stats.adminCount}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Vanliga användare</span>
                            <span className="app-value">{stats.regularUserCount}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Aktiva konton</span>
                            <span className="app-value">{stats.activeCount}</span>
                        </div>
                    </div>
                </div>
            </section>

            <section className="app-card">
                <h2>Användare</h2>
                <p className="app-card-subtitle">
                    Se alla användare och välj en användare för redigering.
                </p>

                {users.length === 0 ? (
                    <p className="app-card-subtitle">Inga användare hittades.</p>
                ) : (
                    <div className="admin-user-list">
                        {users.map((user) => (
                            <div key={user.id} className="app-info-box">
                                <span className="app-label">Namn</span>
                                <span className="app-value">
                  {user.firstName} {user.lastName}
                </span>

                                <span className="app-label">E-post</span>
                                <span className="app-value">{user.email}</span>

                                <span className="app-label">Roll</span>
                                <span className="app-value">{user.role}</span>

                                <span className="app-label">Status</span>
                                <span className="app-value">
                  {user.active ? "Aktiv" : "Inaktiv"}
                </span>

                                <button
                                    className="app-button admin-edit-button"
                                    type="button"
                                    onClick={() => handleSelectUser(user)}
                                >
                                    Redigera
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            <section className="app-card">
                <h2>Redigera användare</h2>
                <p className="app-card-subtitle">
                    Uppdatera vald användares uppgifter eller återställ lösenord.
                </p>

                {!selectedUserId ? (
                    <p className="app-card-subtitle">
                        Ingen användare vald ännu. Klicka på “Redigera” i listan ovan.
                    </p>
                ) : (
                    <>
                        <form className="profile-form" onSubmit={handleUpdateUser}>
                            <div className="profile-form-grid">
                                <div>
                                    <label className="app-label">Förnamn</label>
                                    <input
                                        className="app-input"
                                        type="text"
                                        value={editFirstName}
                                        onChange={(e) => setEditFirstName(e.target.value)}
                                        required
                                    />
                                </div>

                                <div>
                                    <label className="app-label">Efternamn</label>
                                    <input
                                        className="app-input"
                                        type="text"
                                        value={editLastName}
                                        onChange={(e) => setEditLastName(e.target.value)}
                                        required
                                    />
                                </div>

                                <div className="profile-form-full">
                                    <label className="app-label">E-post</label>
                                    <input
                                        className="app-input"
                                        type="email"
                                        value={editEmail}
                                        onChange={(e) => setEditEmail(e.target.value)}
                                        required
                                    />
                                </div>

                                <div>
                                    <label className="app-label">Roll</label>
                                    <select
                                        className="app-input"
                                        value={editRole}
                                        onChange={(e) => setEditRole(e.target.value)}
                                    >
                                        <option value="USER">USER</option>
                                        <option value="ADMIN">ADMIN</option>
                                    </select>
                                </div>

                                <div>
                                    <label className="app-label">Status</label>
                                    <label className="admin-checkbox-row admin-checkbox-spacer">
                                        <input
                                            type="checkbox"
                                            checked={editActive}
                                            onChange={(e) => setEditActive(e.target.checked)}
                                        />
                                        <span>Aktivt konto</span>
                                    </label>
                                </div>
                            </div>

                            <div className="profile-form-actions">
                                <button className="app-button" type="submit" disabled={editLoading}>
                                    {editLoading ? "Sparar..." : "Spara ändringar"}
                                </button>
                            </div>

                            {editError && <p className="app-message-error">{editError}</p>}
                            {editSuccess && <p className="app-message-success">{editSuccess}</p>}
                        </form>

                        <div className="admin-section-divider" />

                        <form className="profile-form" onSubmit={handleResetPassword}>
                            <h3>Återställ lösenord</h3>
                            <p className="app-card-subtitle">
                                Ange ett nytt lösenord för den valda användaren.
                            </p>

                            <div className="profile-form-grid">
                                <div className="profile-form-full">
                                    <label className="app-label">Nytt lösenord</label>
                                    <input
                                        className="app-input"
                                        type="password"
                                        value={resetPasswordValue}
                                        onChange={(e) => setResetPasswordValue(e.target.value)}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="profile-form-actions">
                                <button
                                    className="app-button"
                                    type="submit"
                                    disabled={resetPasswordLoading}
                                >
                                    {resetPasswordLoading ? "Sparar..." : "Uppdatera lösenord"}
                                </button>
                            </div>

                            {resetPasswordError && (
                                <p className="app-message-error">{resetPasswordError}</p>
                            )}
                            {resetPasswordSuccess && (
                                <p className="app-message-success">{resetPasswordSuccess}</p>
                            )}
                        </form>
                    </>
                )}
            </section>

            <section className="app-card">
                <h2>Skapa användare</h2>
                <p className="app-card-subtitle">
                    Lägg till en ny användare i systemet.
                </p>

                <form className="profile-form" onSubmit={handleCreateUser}>
                    <div className="profile-form-grid">
                        <div>
                            <label className="app-label">Förnamn</label>
                            <input
                                className="app-input"
                                type="text"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Efternamn</label>
                            <input
                                className="app-input"
                                type="text"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                                required
                            />
                        </div>

                        <div className="profile-form-full">
                            <label className="app-label">E-post</label>
                            <input
                                className="app-input"
                                type="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Lösenord</label>
                            <input
                                className="app-input"
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Roll</label>
                            <select
                                className="app-input"
                                value={role}
                                onChange={(e) => setRole(e.target.value)}
                            >
                                <option value="USER">USER</option>
                                <option value="ADMIN">ADMIN</option>
                            </select>
                        </div>

                        <div className="profile-form-full">
                            <label className="admin-checkbox-row">
                                <input
                                    type="checkbox"
                                    checked={active}
                                    onChange={(e) => setActive(e.target.checked)}
                                />
                                <span>Aktivt konto</span>
                            </label>
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={createLoading}>
                            {createLoading ? "Skapar..." : "Skapa användare"}
                        </button>
                    </div>

                    {createError && <p className="app-message-error">{createError}</p>}
                    {createSuccess && <p className="app-message-success">{createSuccess}</p>}
                </form>
            </section>

            <section className="app-card">
                <h2>Manuell tidregistrering åt användare</h2>
                <p className="app-card-subtitle">
                    Lägg in ett arbetspass i efterhand åt en användare.
                </p>

                <form className="profile-form" onSubmit={handleManualTimeForUser}>
                    <div className="profile-form-grid">
                        <div className="profile-form-full">
                            <label className="app-label">Användare</label>
                            <select
                                className="app-input"
                                value={manualUserId}
                                onChange={(e) => setManualUserId(e.target.value)}
                                required
                            >
                                <option value="">Välj användare</option>
                                {users.map((user) => (
                                    <option key={user.id} value={user.id}>
                                        {user.firstName} {user.lastName} ({user.email})
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div>
                            <label className="app-label">Datum</label>
                            <input
                                className="app-input"
                                type="date"
                                value={manualWorkDate}
                                onChange={(e) => setManualWorkDate(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Check-in</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualCheckIn}
                                onChange={(e) => setManualCheckIn(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Lunch ut</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualLunchOut}
                                onChange={(e) => setManualLunchOut(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="app-label">Lunch in</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualLunchIn}
                                onChange={(e) => setManualLunchIn(e.target.value)}
                            />
                        </div>

                        <div>
                            <label className="app-label">Check-out</label>
                            <input
                                className="app-input"
                                type="time"
                                value={manualCheckOut}
                                onChange={(e) => setManualCheckOut(e.target.value)}
                                required
                            />
                        </div>

                        <div className="profile-form-full">
                            <label className="app-label">Kommentar</label>
                            <textarea
                                className="app-input"
                                rows="4"
                                value={manualComment}
                                onChange={(e) => setManualComment(e.target.value)}
                                placeholder="Exempel: Registered by admin"
                            />
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={manualLoading}>
                            {manualLoading ? "Sparar..." : "Registrera tid"}
                        </button>
                    </div>

                    {manualError && <p className="app-message-error">{manualError}</p>}
                    {manualSuccess && <p className="app-message-success">{manualSuccess}</p>}
                </form>
            </section>
        </div>
    );
}

export default AdminPage;