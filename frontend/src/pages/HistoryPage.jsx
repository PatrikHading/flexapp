import { useEffect, useMemo, useState } from "react";
import { fetchTimeHistory } from "../services/auth";

const PAGE_SIZE = 10;

const formatDateTime = (value) => {
    if (!value) return "-";

    try {
        const date = new Date(value);

        if (Number.isNaN(date.getTime())) {
            return value;
        }

        return date.toLocaleString("sv-SE");
    } catch {
        return value;
    }
};

const escapeCsvValue = (value) => {
    if (value === null || value === undefined) return "";
    const stringValue = String(value).replace(/"/g, '""');
    return `"${stringValue}"`;
};

function HistoryPage({ user }) {
    const [historyEntries, setHistoryEntries] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    useEffect(() => {
        const loadHistory = async () => {
            try {
                setLoading(true);
                setError("");

                const data = await fetchTimeHistory(page, PAGE_SIZE);

                setHistoryEntries(Array.isArray(data?.content) ? data.content : []);
                setTotalPages(Number.isInteger(data?.totalPages) ? data.totalPages : 0);
                setTotalElements(Number.isInteger(data?.totalElements) ? data.totalElements : 0);
            } catch (err) {
                setError(err.message);
                setHistoryEntries([]);
                setTotalPages(0);
                setTotalElements(0);
            } finally {
                setLoading(false);
            }
        };

        if (user?.id) {
            loadHistory();
        }
    }, [user, page]);

    useEffect(() => {
        setPage(0);
    }, [fromDate, toDate]);

    const filteredEntries = useMemo(() => {
        return historyEntries.filter((entry) => {
            const entryDate = entry.workDate;

            if (!entryDate) {
                return false;
            }

            if (fromDate && entryDate < fromDate) {
                return false;
            }

            if (toDate && entryDate > toDate) {
                return false;
            }

            return true;
        });
    }, [historyEntries, fromDate, toDate]);

    const handleExportCsv = () => {
        if (filteredEntries.length === 0) {
            return;
        }

        const headers = [
            "Datum",
            "Check-in",
            "Lunch ut",
            "Lunch in",
            "Check-out",
            "Arbetade minuter",
            "Lunchminuter",
            "Flex minuter",
            "Manuell registrering",
            "Status",
            "Kommentar",
        ];

        const rows = filteredEntries.map((entry) => [
            entry.workDate ?? "",
            formatDateTime(entry.checkInTime),
            formatDateTime(entry.lunchOutTime),
            formatDateTime(entry.lunchInTime),
            formatDateTime(entry.checkOutTime),
            entry.workedMinutes ?? "",
            entry.lunchMinutes ?? "",
            entry.flexMinutes ?? "",
            entry.manualEntry ? "Ja" : "Nej",
            entry.status ?? "",
            entry.comment ?? "",
        ]);

        const csvContent = [
            headers.map(escapeCsvValue).join(";"),
            ...rows.map((row) => row.map(escapeCsvValue).join(";")),
        ].join("\n");

        const blob = new Blob(["\uFEFF" + csvContent], {
            type: "text/csv;charset=utf-8;",
        });

        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `flexapp-historik-${user?.id}.csv`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
    };

    const handlePreviousPage = () => {
        setPage((currentPage) => Math.max(currentPage - 1, 0));
    };

    const handleNextPage = () => {
        setPage((currentPage) => Math.min(currentPage + 1, totalPages - 1));
    };

    if (loading) {
        return (
            <div className="app-card">
                <h1>Historik</h1>
                <p className="app-card-subtitle">Laddar historik...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="app-card">
                <h1>Historik</h1>
                <p className="app-message-error">{error}</p>
            </div>
        );
    }

    return (
        <div className="page-section">
            <section className="app-card-hero">
                <div className="history-hero-header">
                    <div>
                        <h1 className="app-card-title">Historik</h1>
                        <p className="app-card-subtitle">
                            Här ser du dina tidigare tidrapporter.
                        </p>
                        <p className="app-card-subtitle">
                            Totalt antal poster: {totalElements}
                        </p>
                    </div>

                    <button
                        className="app-button"
                        onClick={handleExportCsv}
                        disabled={filteredEntries.length === 0}
                    >
                        Exportera CSV
                    </button>
                </div>
            </section>

            <section className="app-card">
                <h2>Filter</h2>

                <div className="history-filter-grid">
                    <div>
                        <label className="app-label">Från datum</label>
                        <input
                            className="app-input"
                            type="date"
                            value={fromDate}
                            onChange={(e) => setFromDate(e.target.value)}
                        />
                    </div>

                    <div>
                        <label className="app-label">Till datum</label>
                        <input
                            className="app-input"
                            type="date"
                            value={toDate}
                            onChange={(e) => setToDate(e.target.value)}
                        />
                    </div>

                    <div className="history-filter-actions">
                        <button
                            className="app-button history-clear-button"
                            type="button"
                            onClick={() => {
                                setFromDate("");
                                setToDate("");
                            }}
                        >
                            Rensa filter
                        </button>
                    </div>
                </div>
            </section>

            <section className="app-card">
                <div className="history-pagination">
                    <button
                        className="app-button history-clear-button"
                        type="button"
                        onClick={handlePreviousPage}
                        disabled={page === 0}
                    >
                        Föregående
                    </button>

                    <span className="app-card-subtitle">
                        Sida {totalPages > 0 ? page + 1 : 0} av {totalPages}
                    </span>

                    <button
                        className="app-button"
                        type="button"
                        onClick={handleNextPage}
                        disabled={totalPages === 0 || page >= totalPages - 1}
                    >
                        Nästa
                    </button>
                </div>
            </section>

            {filteredEntries.length === 0 ? (
                <section className="app-card">
                    <p className="app-card-subtitle">
                        Det finns ingen historik som matchar filtret.
                    </p>
                </section>
            ) : (
                filteredEntries.map((entry) => (
                    <section key={entry.id} className="app-card">
                        <div className="history-entry-header">
                            <div>
                                <h2>{entry.workDate || "Okänt datum"}</h2>
                            </div>

                            <div className="history-badges">
                                <span
                                    className={`history-badge ${
                                        entry.manualEntry ? "history-badge-manual" : "history-badge-live"
                                    }`}
                                >
                                    {entry.manualEntry ? "Manuell" : "Automatisk"}
                                </span>

                                {entry.status && (
                                    <span className="history-badge history-badge-status">
                                        {entry.status}
                                    </span>
                                )}
                            </div>
                        </div>

                        <div className="app-grid">
                            <div className="app-info-box">
                                <span className="app-label">Check-in</span>
                                <span className="app-value">
                                    {formatDateTime(entry.checkInTime)}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Lunch ut</span>
                                <span className="app-value">
                                    {formatDateTime(entry.lunchOutTime)}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Lunch in</span>
                                <span className="app-value">
                                    {formatDateTime(entry.lunchInTime)}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Check-out</span>
                                <span className="app-value">
                                    {formatDateTime(entry.checkOutTime)}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Arbetade minuter</span>
                                <span className="app-value">
                                    {entry.workedMinutes ?? "-"}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Lunchminuter</span>
                                <span className="app-value">
                                    {entry.lunchMinutes ?? "-"}
                                </span>
                            </div>

                            <div className="app-info-box">
                                <span className="app-label">Flex minuter</span>
                                <span className="app-value">
                                    {entry.flexMinutes ?? "-"}
                                </span>
                            </div>
                        </div>

                        {entry.comment && (
                            <div className="history-comment-box">
                                <span className="app-label">Kommentar</span>
                                <p className="history-comment-text">{entry.comment}</p>
                            </div>
                        )}
                    </section>
                ))
            )}
        </div>
    );
}

export default HistoryPage;