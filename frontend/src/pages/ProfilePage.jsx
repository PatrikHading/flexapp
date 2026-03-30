import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
    changePassword,
    logoutUser,
    updateMyProfile,
} from "../services/auth";

function ProfilePage({ user, setUser }) {
    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");

    const [profileLoading, setProfileLoading] = useState(false);
    const [profileError, setProfileError] = useState("");
    const [profileSuccess, setProfileSuccess] = useState("");

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [passwordLoading, setPasswordLoading] = useState(false);
    const [passwordError, setPasswordError] = useState("");
    const [passwordSuccess, setPasswordSuccess] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        setFirstName(user?.firstName || "");
        setLastName(user?.lastName || "");
        setEmail(user?.email || "");
    }, [user]);

    const handleProfileUpdate = async (e) => {
        e.preventDefault();
        setProfileError("");
        setProfileSuccess("");

        try {
            setProfileLoading(true);

            const updatedUser = await updateMyProfile({
                firstName,
                lastName,
                email,
            });

            setUser(updatedUser);
            setProfileSuccess("Profilen har uppdaterats.");
        } catch (err) {
            setProfileError(err.message);
        } finally {
            setProfileLoading(false);
        }
    };

    const handleChangePassword = async (e) => {
        e.preventDefault();
        setPasswordError("");
        setPasswordSuccess("");

        if (newPassword !== confirmPassword) {
            setPasswordError("Det nya lösenordet och bekräftelsen matchar inte.");
            return;
        }

        if (newPassword.length < 6) {
            setPasswordError("Det nya lösenordet måste vara minst 6 tecken.");
            return;
        }

        try {
            setPasswordLoading(true);

            await changePassword(currentPassword, newPassword);

            setPasswordSuccess("Lösenordet har uppdaterats. Du behöver logga in igen.");

            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");

            setTimeout(async () => {
                await logoutUser();
                setUser(null);
                navigate("/login");
            }, 1500);
        } catch (err) {
            setPasswordError(err.message);
        } finally {
            setPasswordLoading(false);
        }
    };

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <h1 className="app-card-title">Min profil</h1>
                <p className="app-card-subtitle">
                    Se och uppdatera dina användaruppgifter samt hantera ditt konto.
                </p>
            </section>

            <section className="app-card">
                <h2>Profiluppgifter</h2>
                <p className="app-card-subtitle">
                    Uppdatera ditt namn och din e-postadress.
                </p>

                <form className="profile-form" onSubmit={handleProfileUpdate}>
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
                    </div>

                    <div className="profile-meta-grid">
                        <div className="app-info-box">
                            <span className="app-label">Roll</span>
                            <span className="app-value">{user?.role || "-"}</span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Status</span>
                            <span className="app-value">
                {user?.active ? "Aktiv" : "Inaktiv"}
              </span>
                        </div>

                        <div className="app-info-box">
                            <span className="app-label">Användar-ID</span>
                            <span className="app-value">{user?.id || "-"}</span>
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={profileLoading}>
                            {profileLoading ? "Sparar..." : "Spara profil"}
                        </button>
                    </div>

                    {profileError && <p className="app-message-error">{profileError}</p>}
                    {profileSuccess && <p className="app-message-success">{profileSuccess}</p>}
                </form>
            </section>

            <section className="app-card">
                <h2>Byt lösenord</h2>
                <p className="app-card-subtitle">
                    Ange ditt nuvarande lösenord och välj ett nytt.
                </p>

                <form className="profile-form" onSubmit={handleChangePassword}>
                    <div className="profile-form-grid">
                        <div className="profile-form-full">
                            <label className="app-label">Nuvarande lösenord</label>
                            <input
                                className="app-input"
                                type="password"
                                value={currentPassword}
                                onChange={(e) => setCurrentPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Nytt lösenord</label>
                            <input
                                className="app-input"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                required
                            />
                        </div>

                        <div>
                            <label className="app-label">Bekräfta nytt lösenord</label>
                            <input
                                className="app-input"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                required
                            />
                        </div>
                    </div>

                    <div className="profile-form-actions">
                        <button className="app-button" type="submit" disabled={passwordLoading}>
                            {passwordLoading ? "Sparar..." : "Byt lösenord"}
                        </button>
                    </div>

                    {passwordError && <p className="app-message-error">{passwordError}</p>}
                    {passwordSuccess && <p className="app-message-success">{passwordSuccess}</p>}
                </form>
            </section>
        </div>
    );
}

export default ProfilePage;