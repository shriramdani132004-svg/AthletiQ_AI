import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import Login from "./routes/Login";
import Dashboard from "./routes/Dashboard";
import "./index.css";
import { EventManagementPage, CreateEventPage, EditEventPage, EventDetailsPage } from "./events/eventPages";

ReactDOM.createRoot(document.getElementById("root")).render(<React.StrictMode><BrowserRouter><AuthProvider><Routes><Route path="/login" element={<Login />} /><Route element={<ProtectedRoute />}><Route path="/" element={<Dashboard />} /></Route>            <Route path="/events" element={<EventManagementPage />} />
            <Route path="/events/create" element={<CreateEventPage />} />
            <Route path="/events/:eventId" element={<EventDetailsPage />} />
            <Route path="/events/:eventId/edit" element={<EditEventPage />} />
</Routes></AuthProvider></BrowserRouter></React.StrictMode>);
