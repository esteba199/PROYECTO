import { useState } from 'react'
import './App.css'

function App() {
  const [events, setEvents] = useState([
    {
      id: 1,
      title: 'Project Kickoff',
      date: 'MAY 25, 2026',
      description: 'Initial meeting to discuss project goals, timeline, and deliverables with the core team.',
    },
    {
      id: 2,
      title: 'Design Review',
      date: 'JUN 02, 2026',
      description: 'Reviewing the initial high-fidelity mockups for the new premium user interface.',
    },
    {
      id: 3,
      title: 'Backend Integration',
      date: 'JUN 15, 2026',
      description: 'Connecting the React frontend with the Spring Boot REST API.',
    }
  ]);

  return (
    <>
      <div className="bg-blobs">
        <div className="blob blob-1"></div>
        <div className="blob blob-2"></div>
      </div>
      
      <div className="app-container">
        <header className="header">
          <h1>Agenda Pro</h1>
          <p>Manage your events with elegance and style</p>
        </header>

        <main>
          <div className="agenda-grid">
            {events.map((event) => (
              <article key={event.id} className="glass-panel agenda-card">
                <span className="date">{event.date}</span>
                <h2>{event.title}</h2>
                <p>{event.description}</p>
                <button className="btn-primary">View Details</button>
              </article>
            ))}
            
            <article className="glass-panel agenda-card" style={{ justifyContent: 'center', alignItems: 'center', borderStyle: 'dashed' }}>
              <button className="btn-primary" style={{ alignSelf: 'center' }}>+ Add New Event</button>
            </article>
          </div>
        </main>
      </div>
    </>
  )
}

export default App
