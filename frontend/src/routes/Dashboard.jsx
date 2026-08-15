import { useAuth } from "../auth/AuthContext";

export default function Dashboard() {
    const { user, logout } = useAuth();
    return <main><h1>AthletiQ Dashboard</h1><p>Authenticated successfully.</p>{user &&<section><p>User: {user.firstName} {user.lastName}</p><p>Email: {user.email}</p><p>Role: {user.role}</p></section>}<p><a href="/profile">Profile</a></p>
        <button onClick={logout}>Logout</button></main>;
}