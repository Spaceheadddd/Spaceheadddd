# Handover: AI Receptionist Business (Newcastle, NSW)

> **For any AI or person taking over:** this file holds the full context of the project so far: where it started, what was decided and why, what has been built, what has and hasn't been tested, and what to do next. Read it all before suggesting changes. **The user prefers plain English**, with short sentences and no jargon. Explain technical terms when you use them.

---

## 1. How this started

The user opened the conversation with this message, quoted exactly:

> "I'm looking for a low cost business model where I can rent out servers or something like that without any upfront cost.
>
> I'm broke atm and I can build systems - meaning I'm technology driven via coding and ai.
>
> What's my best bet ?"

**What they came for:** a way to start earning **with no money to invest**, using what they do have: **time and technical skills** (coding, AI, building systems). Their first idea was renting out servers. They later explained they meant wholesale, reselling or white label.

---

## 2. The user

| Item | Detail |
|---|---|
| Money to invest | **None.** Time and skills only. This is a hard rule for every plan. |
| Skills | Coding (Python, JavaScript, React, Docker, Linux), AI, security background |
| Location | Newcastle, NSW, Australia |
| Transport | **No car.** Public transport only (light rail, buses, trains) |
| Business registration | **ABN active** |
| Communication style | Wants **plain English** and step-by-step explanations |
| Stage | Starting from scratch. No customers yet. |

---

## 3. How the conversation went

| # | What the user asked or said | What we answered or decided |
|---|---|---|
| 1 | Low-cost model, like renting servers, with no upfront cost | Selling plain server space is a weak business: big hosts win on price, margins are thin. Better to **sell a service that runs on servers**, paid for by the customer. |
| 2 | "Speak in plain English. I meant wholesale or white label." | White label means you buy from a big company and resell under your own name. Plain hosting resale earns about $5–15 per customer. **Hosting + app + maintenance** earns about $50–300 per customer. |
| 3 | "I have no capital, just time and skills. So: server + app + maintenance?" | Yes. The model is to **build the demo free, get paid upfront, then buy the server** with the customer's money. You never spend your own money. |
| 4 | "Let's plan. Do I need a website?" | Not at the start. The demo sells it. Later, a one-page site on free hosting plus a domain (about $10 a year) is enough. |
| 5 | Newcastle, public transport, bookings-focused | Target **booking businesses** along transport routes. Contact them online first and visit in person later. |
| 6 | "Do a barber." ABN already active | Built **demo 1**: a scripted chat booking demo for a made-up barber. |
| 7 | "Is this just a chatbot? What does AI change? What if they don't take bookings?" | Explained scripted vs real AI. **The phone AI is the easiest thing to sell.** Walk-in shops can buy call answering, a waitlist or review requests instead. |
| 8 | "Let's do that." (the phone AI) | Built **demo 2**: the booking server and AI setup for a phone receptionist using Vapi. |
| 9 | "To add a chatbot to their website, won't I need their code?" | No. It's **one pasted line** in the site settings, or use a booking link, a QR code or social media messages. The phone AI needs **no website at all**. |
| 10 | Asked for a handover file and the code to run locally | This file plus the `ai-receptionist` folder. |

---

## 4. The user's concerns, and the answers

| Concern | Answer agreed |
|---|---|
| No money to start | Free tiers for demos. The customer pays a setup fee **before** any money is spent. |
| No car | Online outreach first (Instagram DMs, email, Facebook). Visit in person along transport routes, grouped by street. |
| "Is it just a chatbot?" | Demo 1 is scripted. Demo 2 uses real AI by voice. The product being sold is real AI. |
| Shops that don't take bookings | Sell call answering, a live waitlist and review requests, or skip them. Ask "Do you take bookings?" early. |
| Needing access to their website code | Not needed. One pasted line, or no website at all for the phone product. |
| Competition from free booking apps (Fresha, Timely) | Don't compete with them. **Sell the receptionist that fills their booking app**, and connect to it later. |

---

## 5. The business model

### What's being sold
An **AI receptionist** for local booking businesses. It answers **phone calls** (main product) and messages 24/7, answers questions, and books appointments.

### Why this and not plain server resale
| | Plain hosting resale | AI receptionist (server + app + maintenance) |
|---|---|---|
| Price per customer | $5–15 a month | $99–149 a month |
| Customers needed for about $1.5k a month | 100–300 | 10–15 |
| Customers leave easily? | Yes, for cheaper hosting | Rarely, because it brings them bookings |
| Uses the user's skills | Barely | Fully |
| Upfront cost | Small | $0 (customer pays first) |

### Pricing (AUD)
| Item | Price |
|---|---|
| Setup | $300 ($150 for the first 3 customers in exchange for a testimonial) |
| Monthly | $99–149 |
| Running cost per shop | About $10–40 a month (server share, AI and voice usage, phone number) |
| Gross margin | About 70–90% |

### How the money flows
```
Customer pays setup + month 1  →  buy phone number + server  →  set up  →  monthly fee covers running costs + profit
```

### Target customers
| Priority | Business type | Why |
|---|---|---|
| 1 | **Barbers** (chosen first) | Busy mid-cut, miss calls, live on bookings |
| 2 | Hair, beauty, nail and lash salons | Same problem |
| 3 | Massage and physio clinics | High-value bookings |
| 4 | Dog groomers | Owner-run, phone-heavy |

### Areas (reachable without a car)
Hunter St and the CBD (light rail) · Darby St, Cooks Hill · Beaumont St, Hamilton · Charlestown and Kotara (bus) · Maitland (train)

---

## 6. Scope

### In scope now (Phase 1: dry test run)
- A working demo that books a fake barber appointment **by voice**
- A short recorded video of the demo to use in sales messages
- Selling to **barbers in Newcastle**
- Manual setup for each customer: edit the prompt and the services and hours in the code

### Out of scope for now (later phases)
- Connecting to Fresha, Square, Timely or Google Calendar
- SMS reminders, review requests, waitlists
- Real AI website chat bubble, and Instagram or Facebook messages
- A dashboard where customers change settings themselves
- Multiple shops on one server (multi-tenant)
- A business website or brand

---

## 7. What has been built

### Overview
| File | What it is | Status |
|---|---|---|
| `chat-demo.html` | **Demo 1.** Scripted chat booking assistant for the made-up "Foreshore Fade Co.", with an owner diary. Runs in any browser. Bookings saved in the browser only. | ✅ Works, visual demo only |
| `server.js` | **Demo 2 backend.** Node.js server with no packages. Gives Vapi two tools (check times, book). Shows the diary at `/bookings`. Saves to `bookings.json`. | ✅ Tested locally |
| `assistant-prompt.md` | AI instructions (shop facts, booking steps, rules) and first spoken line. Paste into Vapi. | ⚠️ Not yet tested on a live call |
| `tools.json` | Definitions of the two Vapi tools. The server URL needs filling in. | ⚠️ Not yet tested on a live call |
| `README.md` | Step-by-step setup (Render + Vapi) | ✅ |
| `HANDOVER.md` | This file | ✅ |

### How demo 2 works
```
Caller ──voice──▶ Vapi (speech + AI) ──tool call──▶ server.js ──▶ bookings.json
                                                        │
Owner ◀──────────────── /bookings web page ◀────────────┘
```

### Server details (for the next AI)
| Item | Detail |
|---|---|
| Endpoint | `POST /vapi/tools`, which reads `message.toolCallList[]` and returns `{ results: [{ toolCallId, result }] }` |
| Tools | `check_availability(service, date)` · `book_appointment(service, date, time, name, phone)` |
| Date and time format | `YYYY-MM-DD`, with 24-hour `HH:MM` |
| Timezone | `Australia/Sydney` |
| Security | Optional `TOOL_SECRET` env var, checked against the `x-vapi-secret` header |
| Shop data | `SHOP` object at the top of `server.js` (services, minutes, prices, hours by day of week) |
| Storage | `bookings.json` file. **Not durable** on free hosting. |
| Port | `PORT` env var, default 3000 |

### Test results
| Test | Result |
|---|---|
| Check free times for Saturday | ✅ Returned 8am–2pm slots |
| Book a skin fade at 10am | ✅ Saved and shown in the diary |
| Book an overlapping time (10:30 during a 45-minute fade) | ✅ Refused correctly |
| Check a Monday (closed) | ✅ Said the shop is closed |
| Real voice call through Vapi | ❌ **Not done yet.** This is the next step. |
| Date line in the prompt (`{{"now" \| date ...}}`) | ⚠️ Unverified Vapi syntax. Check on the first call. |

---

## 8. Dry test run plan (Phase 1)

The goal is to prove the whole flow works, at $0, before talking to any real business.

| Step | Task | Done when | Cost |
|---|---|---|---|
| 1 | Run `node server.js` locally and open `/bookings` | The diary page loads | $0 |
| 2 | Make the local server reachable from the internet (`npx localtunnel --port 3000` or ngrok), or deploy to Render's free plan | You have a public `https://` address | $0 |
| 3 | Create a Vapi account, paste in the prompt and first message, choose a voice | The assistant exists | $0 (free credit) |
| 4 | Add the 2 tools from `tools.json` with the public URL and secret | The tools are saved | $0 |
| 5 | Press **Talk** in Vapi and book a skin fade for Saturday | The booking appears in `/bookings` | $0 |
| 6 | Try edge cases: a closed day, a full day, a weird question, cancelling | The AI handles them sensibly | $0 |
| 7 | Fix the prompt where the AI got things wrong | The calls sound natural | $0 |
| 8 | Record a 60-second video: call, booking, diary | The video is ready to send | $0 |

**Pass criteria:** 5 test calls in a row book correctly, with no made-up times and correct dates.

---

## 9. Full roadmap

| Phase | Goal | Key tasks | Trigger to move on |
|---|---|---|---|
| **1. Dry test run** | Prove it works | Section 8 | Demo video recorded |
| **2. First sales** | 1–3 paying barbers | List 30 barbers on Google Maps, message 10–20 a day, offer founder pricing, visit the ones who reply | First setup fee paid |
| **3. First real setup** | Go live for one shop | Buy an AU number, set up "forward when busy or unanswered" on the shop's phone, move to a paid server ($5–10 a month), make storage durable, customise the prompt | The shop gets real bookings |
| **4. Make it repeatable** | Setup takes under 1 hour | Template per shop, multiple shops on one server, SMS confirmations to the owner | 5+ customers |
| **5. Add value** | Raise price and keep customers | Booking app or calendar integration, reminders, review requests, website chat bubble, Instagram/Facebook | 10+ customers |
| **6. Expand** | New business types | Salons, physios, groomers using the same setup | Steady monthly income |

---

## 10. Sales kit

**Outreach message (draft):**
> "Hi, I'm a local developer in Newcastle. I build AI receptionists for barbers that answer your calls and messages 24/7 and book people in, even when you're mid-cut. Here's a 1-minute demo: [video]. $300 setup and $129 a month, and I look after everything. Can I show you this week?"

**Key questions to ask every shop:**
1. Do you take bookings, or are you walk-in only?
2. How many calls or messages do you miss in a day?
3. What do you use for bookings now (Fresha, Square, paper)?

**Main pitch:** *"I answer your phone when you're busy, and book people in."*

---

## 11. Risks and open questions

| Risk or question | Notes |
|---|---|
| Free hosting sleeps and wipes `bookings.json` | Fine for a demo. Fix in Phase 3 with a paid server or database. |
| Vapi pricing per minute | Check current rates before quoting. Build usage into the monthly price. |
| AI makes up times or wrong dates | The prompt forbids it. Test well in Phase 1. |
| Privacy (customer names and numbers) | Keep data minimal. Consider Australian privacy obligations as the business grows. |
| Finding customers is the hard part | Most time should go to outreach, not building. |
| Call forwarding setup differs by phone carrier | Learn the steps for Telstra, Optus and Vodafone before the first install. |

---

## 12. Rules for the next AI
1. **No upfront spending by the user.** Anything that costs money waits until a customer pays.
2. **Plain English.** Explain every step, and make it suitable for someone on public transport doing outreach from a phone.
3. **Finish Phase 1 before building new features.** The next milestone is a working voice call booking.
4. Keep `server.js` free of extra packages unless there's a strong reason.
5. Don't present scripted demos as real AI to customers.
