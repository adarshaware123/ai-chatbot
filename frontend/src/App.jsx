
import { useState } from "react";
import "./App.css";

function App() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [conversationId, setConversationId] = useState(null);
  const [loading, setLoading] = useState(false);

  async function startConversation() {
    const response = await fetch("http://localhost:8080/api/chat/new", {
      method: "POST",
    });

    if (!response.ok) {
      throw new Error("Could not start conversation");
    }

    const data = await response.json();
    setConversationId(data.conversationId);
    return data.conversationId;
  }
  function newChat() {
    setMessages([]);
    setConversationId(null);
    setInput("");
  }

  async function sendMessage(event) {
    event.preventDefault();

    const message = input.trim();
    if (!message || loading) return;

    setMessages((previous) => [
      ...previous,
      { role: "user", content: message },
    ]);
    setInput("");
    setLoading(true);

    try {
      let id = conversationId;

      if (!id) {
        id = await startConversation();
      }

      const response = await fetch(
          `http://localhost:8080/api/chat?conversationId=${encodeURIComponent(id)}`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ message }),
          }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || "Something went wrong");
      }

      setMessages((previous) => [
        ...previous,
        { role: "assistant", content: data.aiResponse },
      ]);
    } catch (error) {
      setMessages((previous) => [
        ...previous,
        { role: "assistant", content: `Error: ${error.message}` },
      ]);
    } finally {
      setLoading(false);
    }
  }

  return (
      <main className="chat-container">
        <header className="chat-header">
          <h1>AI ChatBox</h1>
          <p>Your personal AI assistant</p>
          <button type="button" onClick={newChat}>
            + New Chat
          </button>
        </header>

        <section className="messages">
          {messages.length === 0 && (
              <p className="welcome">Hi! Ask me anything.</p>
          )}

          {messages.map((message, index) => (
              <div key={index} className={`message ${message.role}`}>
                <strong>{message.role === "user" ? "You" : "AI"}</strong>
                <p>{message.content}</p>
              </div>
          ))}

          {loading && <p className="loading">AI is thinking...</p>}
        </section>

        <form className="chat-form" onSubmit={sendMessage}>
          <input
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder="Type your message..."
              maxLength={2000}
              disabled={loading}
          />
          <button type="submit" disabled={loading || !input.trim()}>
            Send
          </button>
        </form>
      </main>
  );
}

export default App;
