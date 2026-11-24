# Handover: AI Receptionist Business

## 1. Where you're starting from
- **No money to invest**, only time and skills (coding and AI).
- Based in **Newcastle, NSW, Australia**. No car, so you rely on public transport.
- **ABN is active.**
- First idea was renting out servers: wholesale, reselling or white label.

## 2. What we talked about, in order

**"I want to rent out servers with no upfront cost."**
Selling plain server space against Hetzner, AWS and the big hosts is a losing game: thin margins, price shopping and lots of support work. You can still resell servers (white label). The trick is to **wrap the server in a service**.

**"Explain in plain English. I meant wholesale or white label."**
White label means you buy from a big company and sell under your own name. For example, rent one server for $10 a month and put many customers on it.
Plain hosting earns about $5–15 per customer. **Server + app + maintenance** earns about $50–300 per customer, and those customers stay longer.

**"I have no capital, just time and skills."**
How you start at $0:
1. Build the demo on free tools.
2. Show it to businesses.
3. **Get paid upfront** (setup fee plus first month).
4. Use that money to rent the server.
The customer's money always comes in before you spend any.

**"Do I need a website?"**
Not at the start. The demo sells it. Later, a one-page site on free hosting plus a domain (about $10 a year) is enough.

**Decision: bookings, starting with barbers.**
Barbers are busy cutting and miss calls and messages.
Many already use free booking apps like Fresha or Timely. **Don't sell them a booking app.** Sell the assistant that answers people and fills bookings.

**"Is this just a chatbot? What does AI change? What if they don't take bookings?"**
- The first demo is a scripted chatbot that follows rules I wrote.
- Real AI understands normal messy messages, answers anything about the shop, and can work on the phone, SMS, Instagram and Facebook.
- **Answering the phone is the easiest thing to sell**, because barbers can't pick up mid-cut.
- For walk-in-only barbers, sell call answering, a live waitlist and review requests instead, or skip them.

**"To add a chatbot to their website, won't I need their code?"**
No. It's one line pasted into their site settings (Wix, Squarespace, WordPress, Shopify). No website? Use a booking link, a QR code or Instagram/Facebook messages.
The **phone AI needs no website at all**. They forward missed calls to your number.

## 3. Your concerns, and the answers
| Concern | Answer |
|---|---|
| No money | Customer pays upfront, then you buy what you need. Demos use free tiers. |
| No car | Contact businesses online first. Visit along the light rail and bus routes: Hunter St, Darby St, Beaumont St in Hamilton, Charlestown, Kotara, Maitland by train. |
| Is it really AI? | The chat demo is scripted. The phone version uses real AI (Vapi). |
| Not every shop takes bookings | Sell call answering, a waitlist or reviews, or move on. Ask "Do you take bookings?" first. |
| Need their website code? | No. One pasted line, or skip the website and use the phone. |

## 4. The offer
- **Product:** an AI receptionist that answers calls and messages 24/7 and books people in.
- **Price:** $300 setup ($150 for the first 3 customers in exchange for a review) plus **$99–149 a month**.
- **Your costs:** about $10–40 a month per shop (server, AI usage, phone number).
- **Goal:** 10 shops is about $1,000–1,500 a month.

## 5. What was built (this folder)
| File | What it is |
|---|---|
| `chat-demo.html` | Scripted chat booking demo for the made-up "Foreshore Fade Co." Open it in a browser. |
| `server.js` | Booking server for the phone AI. Checks free times, books, and shows the diary at `/bookings`. No packages needed. |
| `assistant-prompt.md` | The AI's instructions and first message. Paste them into Vapi. |
| `tools.json` | The two actions the AI can take (check times, book). |
| `README.md` | Step-by-step setup for Render and Vapi. |

Tested: free times, booking, clash blocking, and closed days all work.
Not yet tested: a real call through Vapi. The date line in the prompt uses Vapi's own format, so check it on your first call.

## 6. Run it on your computer
You need Node.js installed (nodejs.org).
```
cd ai-receptionist
node server.js
```
- Open `http://localhost:3000/bookings` to see the diary.
- Open `chat-demo.html` by double-clicking it.
- To let Vapi reach your computer, run `npx localtunnel --port 3000` (or ngrok) and use the address it gives you as the server URL in Vapi. Or deploy to Render, as the README explains.

## 7. Next steps
1. Get the Vapi voice demo working ("Talk" in the browser) and book a skin fade.
2. Record a 60-second video: call → booking → diary.
3. List 30 barbers on Google Maps.
4. Message 10–20 a day. Draft:
   > "Hi, I'm a local developer in Newcastle. I build AI receptionists for barbers that answer your calls and messages 24/7 and book people in, even when you're mid-cut. Here's a 1-minute demo: [video]. $300 setup and $129 a month, and I look after everything. Can I show you this week?"
5. Ask everyone: **"How many calls or messages do you miss a day?"**
6. First customer pays → buy a phone number → set up call forwarding → change the shop details in the prompt and `server.js`.

## 8. Later
- Real AI version of the website chat (paste-in chat bubble).
- Connect to their existing booking app (Fresha, Square, Timely) and calendar.
- SMS reminders and Google review requests.
- Reuse the same setup for salons, physios, dog groomers and other booking businesses.
