import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes, useParams } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthContext";
import ProtectedRoute from "./routes/ProtectedRoute";
import Login from "./routes/Login";
import ProfilePage from "./profile/ProfilePage";
import FormBuilderPage from "./form-builder/FormBuilderPage";
import Register from "./routes/Register";
import Dashboard from "./routes/Dashboard";
import "./index.css";
import "./polished-ui.css";
import PublicApplicationPage from "./public-application/PublicApplicationPage";
import EventApplicationsPage from "./applications/EventApplicationsPage";
import { EventManagementPage, CreateEventPage, EditEventPage, EventDetailsPage, EventEvaluationPage } from "./events/eventPages";


function FormBuilderRoute() {
    const { eventId } = useParams();
    const { user } = useAuth();

    if (!user?.userId) {
        return <main><p>Authentication required.</p></main>;
    }

    return (
        <FormBuilderPage
            eventId={Number(eventId)}
            organizerId={user.userId}
        />
    );
}
ReactDOM.createRoot(document.getElementById("root")).render(<React.StrictMode><BrowserRouter><AuthProvider><Routes><Route path="/apply/:publicCode" element={<PublicApplicationPage />} />
<Route path="/login" element={<Login />} />
<Route path="/register" element={<Register />} /><Route element={<ProtectedRoute />}><Route path="/" element={<Dashboard />} />
<Route path="/profile" element={<ProfilePage />} /></Route>            <Route path="/events" element={<EventManagementPage />} />
            <Route path="/events/create" element={<CreateEventPage />} />
            <Route path="/events/:eventId" element={<EventDetailsPage />} />
<Route path="/events/:eventId/evaluation" element={<EventEvaluationPage />} />
<Route path="/events/:eventId/applications" element={<EventApplicationsPage />} />
<Route path="/events/:eventId/form-builder" element={<FormBuilderRoute />} />
            <Route path="/events/:eventId/edit" element={<EditEventPage />} />
</Routes></AuthProvider></BrowserRouter></React.StrictMode>);
