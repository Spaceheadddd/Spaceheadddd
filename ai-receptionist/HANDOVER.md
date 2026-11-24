# Project Handover: Managed Tech Services Business

| | |
|---|---|
| **Document type** | Project handover and business plan |
| **Version** | 3.0 (replaces v1 and v2) |
| **Date** | 24 November 2026 |
| **Base** | Newcastle, NSW, Australia |
| **Status** | Direction agreed · first niche chosen · two demos built · lead system designed, not built · no clients yet |
| **Files** | `ai-receptionist/` folder, git branch `ccr-dc5207a9-w25myz` |
| **Currency** | AUD unless marked USD |

> **For the next AI or person taking over:** the owner wants **plain English**, honest advice and clear steps. The hard rules are **$0 upfront, very low running costs, and the highest margin possible, short term to long term**. Read the document in order. Each section builds on the one before it.

### Rating key used in this document
| Symbol | Meaning |
|---|---|
| ★★★★★ | Ideal for us |
| ★★★★☆ | Very good |
| ★★★☆☆ | OK, with trade-offs |
| ★★☆☆☆ | Weak. Something else is better. |
| ★☆☆☆☆ | Avoid |
| ✅ / ⚠️ / ❌ | Use now / use later or with care / don't use |
| 💬 | A figure **discussed in the chat** |
| 💡 | A figure **suggested in this document** (new, to be tested) |

---

## Contents
**Part A: The starting point**
1. Executive summary: is this profitable?
2. The owner and the brief
3. Discussion record: what was discussed and decided

**Part B: The business**
4. The problem we solve
5. The business model: server + app + maintenance
6. Why servers add value
7. The apps we can sell
8. White label explained
9. Positioning

**Part C: The market**
10. Niches, ranked
11. The starting offer and add-ons

**Part D: The money**
12. Pricing, explained
13. Costs
14. Profit per client
15. Workload: what the work involves
16. Projections: first income, middle stage, long run
17. Is it profitable? Verdict and decision guide
18. Other options compared

**Part E: Doing it**
19. Tools and sources, rated
20. Getting clients: the automated lead system
21. What's been built: demos and dry test run
22. Roadmap
23. Scope
24. Risks and compliance
25. Handover rules, open questions, next actions

---

# Part A: The starting point

## 1. Executive summary: is this profitable?

**Short answer: yes, if clients keep coming in.** The model has almost no fixed costs and **margins of about 75–90%**, and most of the income repeats every month. Its limits are not money. **They are sales and the owner's time.**

| Question | Answer |
|---|---|
| What's the business? | We **set up, host and run** tech that local businesses need (websites, booking systems, AI chatbots, automations, internal tools) on **our own cheap servers**, and charge a **monthly retainer**. |
| What's the upfront cost? | **$0.** Demos run on free tiers. Each client pays a setup fee **before** we spend anything. |
| What's the running cost? | About **$0–40 a month** in total at the start, plus about $5–25 a month per client in usage. |
| First niche? | **Booking-based local businesses** in Newcastle. Two niches share first place: **barbers and salons**, and **allied health clinics**. |
| When does the first income arrive? | Expected **month 2** (about $270: a setup fee plus the first month) |
| Year 1 (expected, 10–15 hrs a week) | About **22 clients** and about **$2,600 a month** recurring by month 12. About **$17k** profit over the year. |
| Long run (years 2–3) | **$5k–9k a month** recurring is possible with 40–60 clients, but it needs automation, more hours or help |
| Biggest risk | Not finding clients fast enough. Section 20 covers an automated lead system to fix this. |

How the rest of the document flows: **who the owner is (2) → what we discussed (3) → the problem (4) → the model (5–9) → who we sell to (10–11) → the money (12–18) → how we do it (19–25).**

---

## 2. The owner and the brief

### 2.1 The brief, in the owner's words
> "I'm looking for a low cost business model where I can rent out servers or something like that without any upfront cost.
>
> I'm broke atm and I can build systems - meaning I'm technology driven via coding and ai.
>
> What's my best bet ?"

Later clarified: by "rent out servers" they meant **wholesale, reselling or white label**.

### 2.2 The brief, stated precisely
| Requirement | Meaning for the plan |
|---|---|
| **Low cost, no upfront cost** | Nothing is bought until a client pays |
| **Very low operational cost** | Shared servers, free tiers and pay-per-use services only |
| **Most margin, short to long run** | Recurring monthly income, with costs that barely grow per client |
| **Servers / white label** | Infrastructure is the base of the business |
| **Use the owner's assets** | **Time, skills and energy**: coding, AI, building systems |

### 2.3 The owner
| Item | Detail |
|---|---|
| Money to invest | None |
| Skills | Coding (Python, JS, React), Docker, Linux, AI, security and bug bounty background |
| Time available | **10–15 hours a week minimum**. Varies. |
| Location | Newcastle, NSW |
| Transport | Public transport only. No car. |
| Registration | **ABN active** |
| Communication | Plain English, step by step |

This brief shapes everything that follows. Section 3 shows how the conversation turned it into a direction.

---

## 3. Discussion record: what was discussed and decided

### 3.1 Key discussions
| # | Topic | Owner's question or point | What was suggested | Decision |
|---|---|---|---|---|
| 1 | **Best bet** | Rent out servers with no upfront cost | Don't sell raw servers. **"Rent outcomes that happen to run on servers."** Set up and run something a business needs, host it on a cheap VPS ($5–20 a month) and charge a retainer ($50–300 a month). | ✅ Owner: "seems good" |
| 2 | **What to sell** | — | Options: AI chatbot or receptionist · automation as a service (n8n) · managed open-source apps · micro-SaaS later | All kept as the service menu |
| 3 | **What to avoid** | — | Buying or reselling GPUs or servers · generic hosting resale · building before anyone has paid | ✅ Agreed |
| 4 | **White label** | "I meant wholesale or white label" | White label means buying from a big company and selling under your brand. Plain hosting is a commodity, so **wrap the server in a service**. | ✅ Owner: **"server + app + maintenance"** |
| 5 | **No capital** | "No capital, only time and skills" | Free demo → show businesses → **get paid upfront** → rent the server → add more clients to it | ✅ **$0-upfront rule** |
| 6 | **Website?** | "Do I need a website?" | Not at first, since the demo sells. Later a free-hosted one-page site plus a domain (about $10 a year). | ✅ |
| 7 | **Location** | Newcastle, public transport | Outreach online first. Visits grouped along light rail, bus and train routes. | ✅ |
| 8 | **Niche** | "Bookings oriented", then "do a barber" | Bookings niche. Barber as the first demo. Sell what **fills** bookings, not another booking app. | ✅ Niche chosen to start |
| 9 | **AI vs chatbot** | "Is this just a chatbot? What changes with AI?" | The demo is scripted. Real AI understands any wording and works across channels. | ✅ |
| 10 | **Non-booking businesses** | "What if they don't do bookings?" | Sell enquiry handling, a waitlist and reviews, or skip them | ✅ |
| 11 | **Phone AI** | "Let's do that" | Built a phone-answering demo | ✅ **Phone = add-on service** |
| 12 | **Install worry** | "Won't I need their code?" | No. One pasted line, or a booking link, QR code or social media messages. | ✅ |
| 13 | **Lead strategy** | Owner's own idea | Automatically collect businesses, check for existing assistants, profile them, find emails, and track outreach with automatic emails | ✅ Section 20 |

### 3.2 Concerns raised and how they're handled
| Concern | Handled by |
|---|---|
| No money | $0-upfront rule. Free tiers (Section 19). |
| No car | Automated online lead finding (Section 20). Visits on transport routes. |
| Is it real AI? | The product uses real AI. Demos are labelled honestly (Section 21). |
| Businesses without bookings | Other apps from the menu (Section 7) |
| Access to their code | One-line embed, or no website needed |
| Existing competitors and tools | Profile them and improve on them (Section 20.4) |

The direction is set. The next question is what business problem we're actually solving.

---

# Part B: The business

## 4. The problem we solve

Small local businesses need technology but **don't have the time, skills or staff** to set it up and keep it running.

| Problem | What it costs them | What we provide |
|---|---|---|
| Missed calls and messages while busy | Lost customers and bookings | AI chatbot, phone add-on |
| Slow or manual bookings | Admin time, no-shows | Booking system, reminders |
| Old, slow or missing website | Look unprofessional, can't be found | Managed website |
| Repetitive admin (invoices, follow-ups, data entry) | Hours every week | Automation workflows |
| Scattered spreadsheets and paper | Errors, wasted time | Simple internal tools |
| Tech that breaks with nobody to fix it | Stress, downtime | Ongoing maintenance |
| Too many subscriptions that don't connect | Cost and confusion | One provider, connected systems |

**In one line:** they want the result (more customers, less admin), not the tech. We take the tech off their hands.

To deliver this cheaply and repeatedly, we need a model. That's next.

---

## 5. The business model: server + app + maintenance

### 5.1 The model
> **We set up and run something a business needs: a website, booking system, internal tool, AI chatbot or automation workflow. We host it on a cheap VPS ($5–20 a month) and charge a monthly retainer ($50–300 a month per client).** 💬

```
   ┌───────────┐     ┌───────────────────────┐     ┌──────────────┐
   │  SERVER   │  +  │         APP           │  +  │ MAINTENANCE  │   =  one monthly fee
   │ we rent & │     │ website, booking,     │     │ updates,     │
   │ share it  │     │ chatbot, automation…  │     │ fixes, help  │
   └───────────┘     └───────────────────────┘     └──────────────┘
```

| Part | What it is | Who pays |
|---|---|---|
| **Server** | A cheap online computer (VPS) we rent and share across many clients | Paid from client fees |
| **App** | The tool the client needs. We build it or set up an open-source one. | Setup fee |
| **Maintenance** | Keeping it running, updating it, small changes, support | Monthly retainer |

### 5.2 How money flows with $0 upfront
```
Free demo ─▶ Client says yes ─▶ Client pays setup + month 1 ─▶ We rent/extend server ─▶ Monthly retainer = profit
```
We never spend before we're paid. Every new client on the same server costs almost nothing extra, **which is where the margin comes from.**

The server is the part that ties this together. Section 6 explains why it matters.

---

## 6. Why servers add value

**Plain-English version:** the server is the shop where the client's tools live. Because we own the shop, the client keeps paying us rent and service every month, and we can fit many clients into one shop for very little money.

| Without our own server | With our own server |
|---|---|
| We build something, hand it over, and get paid once | **We host it, so we get paid every month** |
| The client pays for 5 different subscriptions | The client pays us once a month for everything |
| Each client costs us a full tool subscription | **Many clients share one $5–20 server** |
| We can't easily fix or improve it | We can update, fix and back up everything centrally |
| The client can easily leave | Switching away is effort for them, so they **stay longer** |
| Limited by other companies' pricing | **We set the price and keep the margin** |

**Why it's worth it, summed up:**
1. **Recurring income.** Hosting turns one-off work into monthly income.
2. **Margin.** One server holds many clients, so each one costs a few dollars.
3. **Control.** We decide the features, the price and the quality.
4. **Stickiness.** Clients rarely move a service that just works.
5. **It's what the owner wanted.** It's the "rent out servers" idea, sold as a full service instead of bare space.

So what do we actually put on those servers? Section 7.

---

## 7. The apps we can sell

"App" means **any tool a business uses** that we can set up, host and look after.

| App type | What it does for the business | How we build it | Effort to set up | Ideal niches | Rating for us |
|---|---|---|---|---|---|
| **AI chatbot / receptionist** | Answers questions 24/7, captures leads, books | Our own code + AI API | Medium | Salons, clinics, trades | ★★★★★ |
| **Booking system** | Online bookings, calendar, reminders | Open source (Cal.com, Easy!Appointments) or our own | Low–medium | Salons, clinics, PTs, tutors | ★★★★★ |
| **Managed website** | Professional site, fast, kept up to date | WordPress or static site | Low | Everyone | ★★★★☆ |
| **Automation workflows** | Automates admin: invoices, follow-ups, forms → spreadsheets, reminders | n8n (self-hosted) | Low–medium | Everyone, especially trades and clinics | ★★★★★ |
| **Internal tools** | Custom dashboards, job trackers, stock lists, staff rosters | Our own code (React + database) | Medium–high | Trades, retail, small offices | ★★★★☆ |
| **Review and reputation tools** | Asks for Google reviews and tracks feedback | Automation + SMS/email | Low | Everyone | ★★★★☆ |
| **Customer reactivation** | "Time for your next visit" messages | Automation + SMS/email | Low | Salons, dental, physio | ★★★★☆ |
| **Managed open-source apps** | File sharing (Nextcloud), analytics (Plausible/Umami), uptime monitoring (Uptime Kuma) | Docker on our server | Low | Offices, agencies | ★★★☆☆ |
| **Phone AI (add-on)** | Answers calls by voice and books or takes messages | Vapi/Retell + our server | Medium | Clinics, trades, salons | ★★★★☆ add-on |
| **Website security checks** | Regular checks and fixes for basic website security | The owner's security skills + tools | Low | Everyone with a website | ★★★★☆ add-on |
| **Micro-SaaS** (later) | One narrow tool sold to many businesses in a niche | Our own product | High | One niche | ★★★☆☆ later |

**Bundling:** the best clients use several apps, for example website + chatbot + booking + reviews. More apps mean a higher monthly fee and less chance they leave.

Some of these could be bought ready-made and rebranded instead of built. That's white label.

---

## 8. White label explained

### 8.1 What it is
**White label** means a company makes a product and lets you sell it **under your own name**. The customer only ever sees your brand.

There are two sides to this for us:

| Direction | Meaning | When |
|---|---|---|
| **A. We buy white label** | Rent someone else's platform, rebrand it, resell it | Only if it saves real time, and only once clients pay |
| **B. We become the white-label provider** | Other businesses (web designers, agencies) resell **our** platform under their brand | Later, once our platform is stable |

### 8.2 Direction A: white-label services we could buy
| Option | What you get | Approx. cost | Model | Rating | Verdict |
|---|---|---|---|---|---|
| **Reseller hosting** (ResellerClub, Hostinger, cPanel resellers) | Web hosting space to split and resell | About $15–40 a month | Buy wholesale space, sell small plans for $5–15 each | ★★☆☆☆ | ❌ Commodity, low margin. Our own VPS is cheaper and more flexible. |
| **GoHighLevel** (agency plans) | Full white-label CRM, booking, chat, SMS and funnel platform | About USD $97–497 a month | Rebrand it and charge clients $100–300 a month | ★★★☆☆ | ⚠️ Powerful but costs money before any client. Consider at 10+ clients. |
| **White-label chatbot platforms** (Chatbase, Botpress and similar) | Ready-made AI chatbots | About USD $40–500 a month | Rebrand, charge per client | ★★☆☆☆ | ❌ We can build this ourselves at lower cost |
| **White-label voice AI** (Vapi, Retell, used as building blocks) | Voice engine, pay per minute | Usage only (cents a minute) | Built into our phone add-on | ★★★★☆ | ✅ For the phone add-on only |
| **White-label website builders** (Duda and similar) | Website platform for agencies | About USD $20–100+ a month | Build client sites on it | ★★☆☆☆ | ❌ WordPress or static sites on our server are free |

**Rule:** a white-label tool is worth it only when **(cost per month) is far less than (the hours it saves × your hourly value)** and there are already clients paying for it.

### 8.3 Direction B: us as the white-label provider (long run)
| Item | Detail |
|---|---|
| **Ideal partners** | Freelance web designers, small marketing agencies, IT support businesses, business coaches. They have clients but can't build AI or automations. |
| **What they get** | Our chatbot, booking and automation platform with their logo |
| **Model** | They pay us a wholesale price (💡 e.g. $40–60 per client a month) and charge their clients $100–200 |
| **Why it's good** | We get many clients without doing the selling |
| **When** | After 15–20 direct clients and a stable platform (Section 22) |

**Takeaway:** build our own platform on our own servers (most margin, $0 upfront). Use white label only for hard parts like voice. Later, become the white-label provider ourselves.

That raises the question of how we present ourselves in the market.

---

## 9. Positioning

| Element | Statement |
|---|---|
| **Who we are** | A local Newcastle tech partner for small businesses |
| **What we do** | We set up, host and look after the tech that brings you customers and saves you admin |
| **For whom** | Busy owner-run businesses with 1–20 staff and no IT person |
| **Main promise** | "You run your business. We run your tech." |
| **Why us, not a big agency** | Local, affordable monthly fee, one contact, fast fixes, no tech talk |
| **Why us, not DIY apps** | Done for you, connected together, looked after, with AI built in |
| **How we start conversations** | With one sharp offer for one niche (Section 11), then grow the account with more apps |

**Competitor angle:** many businesses already use tools like Fresha, HotDoc or a chat widget. **We don't replace them. We add what's missing and connect things up.**

With the model and positioning clear, the next step is choosing who to sell to first.

---

# Part C: The market

## 10. Niches, ranked

### 10.1 Scoring criteria
Each niche is scored 1–5 on: **pain** (how badly they need it), **budget**, **speed to sell**, **fit with our apps**, **ease of reaching them without a car**, and **low compliance risk**.

### 10.2 Ranking
| Rank | Niche | Pain | Budget | Speed | Fit | Reach | Low risk | **Total /30** | Status |
|---|---|---|---|---|---|---|---|---|---|
| **1=** | **Barbers & hair/beauty salons** | 4 | 3 | 5 | 5 | 5 | 5 | **27** | ✅ Start (demo built) |
| **1=** | **Allied health clinics** (physio, chiro, massage, podiatry, osteo, psych) | 5 | 5 | 3 | 5 | 4 | 3 | **25** | ✅ Start alongside salons |
| 3 | Trades (plumbers, electricians, HVAC) | 5 | 4 | 4 | 4 | 3 | 5 | **25** | ⚠️ Next. Strong phone add-on fit. |
| 4 | Dental clinics | 5 | 5 | 2 | 4 | 4 | 3 | **23** | ⚠️ Later. Often already have tools. |
| 5 | Vets & pet groomers | 4 | 4 | 3 | 4 | 4 | 4 | **23** | ⚠️ Later |
| 6 | Gyms, studios, personal trainers | 3 | 3 | 4 | 4 | 4 | 5 | **23** | ⚠️ Later |
| 7 | Driving schools, tutors, mechanics | 4 | 2 | 4 | 4 | 4 | 5 | **23** | ⚠️ Later |
| 8 | Real estate / property management | 3 | 5 | 2 | 3 | 4 | 4 | **21** | ❌ Longer sales cycle |
| 9 | Restaurants & cafes | 3 | 2 | 3 | 3 | 5 | 5 | **21** | ❌ Low budget, many existing tools |

*Barbers and salons are joint first because they're fast to sell and already have a demo. Clinics are joint first on value per client. Both are run in parallel and compared after the first 100 contacts each.*

### 10.3 Joint first niche A: barbers & salons
| Item | Detail |
|---|---|
| Who | Barbers, hair salons, nails, lashes, beauty. Usually owner-run with 1–6 chairs. |
| Main pain | Busy with a client, so they miss calls and DMs. No-shows. |
| Tools they already use | Fresha, Timely, Square, Kitomba, or paper and phone |
| Lead app | **AI chatbot + bookings** on their website, Instagram and Facebook |
| Add-ons | Reminders, review requests, reactivation, phone AI |
| Where (no car) | Hunter St & CBD (light rail) · Darby St · Beaumont St, Hamilton · Charlestown/Kotara (bus) · Maitland (train) |
| Watch-outs | Smaller budgets. Some are walk-in only. |

### 10.4 Joint first niche B: allied health clinics
| Item | Detail |
|---|---|
| Who | Physio, chiro, massage, podiatry, osteo, psychology. 1–15 practitioners. |
| Main pain | Reception overloaded, after-hours enquiries lost, admin heavy |
| Tools they already use | Cliniko, Nookal, Halaxy, HotDoc, HealthEngine |
| Lead app | **AI chatbot for enquiries + new-patient booking requests** |
| Add-ons | Phone AI, reminders, reactivation ("time for your follow-up"), reviews, intake automation |
| Watch-outs | **Health privacy.** The AI must never give medical advice. Store only minimal details. |
| Why it's worth it | High value per booking, so they can pay more |

The niche tells us who to sell to. Next is exactly what we offer them first.

---

## 11. The starting offer and add-ons

### 11.1 Starting offer (both niches)
**"Never miss a booking or enquiry again."** An AI chatbot that answers customers 24/7, captures their details, and books them in or sends the owner a booking request. It's set up, hosted and looked after by us.

### 11.2 Add-on menu
| Add-on | What it adds | Best niche |
|---|---|---|
| **Phone AI** | Answers missed or after-hours calls by voice | Clinics, trades, salons |
| Reminders | SMS or email before appointments, fewer no-shows | Clinics, salons |
| Review requests | Asks happy customers for Google reviews | Everyone |
| Reactivation | Brings back customers who haven't returned | Salons, clinics |
| Managed website | New or rebuilt site, hosted by us | Weak or no website |
| Automation pack | Admin tasks automated (forms, invoices, follow-ups) | Clinics, trades |
| Security check | Monthly website security check and fixes | Everyone |

**Land-and-expand:** start each client on one app, then add more over time. Each add-on raises the monthly fee with very little extra cost. That's how average income per client grows in the projections.

Now we can price it.

---

# Part D: The money

## 12. Pricing, explained

### 12.1 How we price
| Principle | Why |
|---|---|
| **Setup fee upfront** | Covers our time and any costs, so we never spend first ($0-upfront rule) |
| **Monthly retainer** | Recurring income, which pays for hosting, maintenance and support |
| **Price on value, not cost** | One extra booking a week is worth $40–150+ to them, so $129 a month is easy to justify |
| **Founder discount for the first 3 clients** | Easier first sales, in exchange for testimonials |
| **Usage caps on AI and phone** | Protects the margin if a client uses a lot |

### 12.2 Price list
| Service | Setup | Monthly | Source |
|---|---|---|---|
| General model (any app) | — | **$50–300** | 💬 |
| Early example quote | $200 | $100 | 💬 |
| **AI chatbot + bookings (starting offer)** | **$300** ($150 for the first 3) | **$99–149** | 💬 |
| Managed website | $300–800 | $50–80 | 💡 |
| Booking system on its own | $200–300 | $50–99 | 💡 |
| Automation workflow | $200–500 per workflow | $50–150 | 💡 |
| Internal tool | $500–1,500 | $100–300 | 💡 |
| Managed open-source app | $100–200 | $30–100 | 💡 |
| **Phone AI add-on** | +$150 | +$99–199 (includes a set number of minutes) | 💡 |
| Reminders / reviews / reactivation | +$0–100 | +$29–59 each | 💡 |
| Website security check | — | +$29–49 | 💡 |

### 12.3 Why these prices
- **$99–149 a month** sits under the cost of a part-time receptionist (thousands a month), and is roughly what businesses already pay for single tools like Fresha or Mailchimp. But we do the work for them.
- **The $300 setup** is low enough to say yes to, and high enough to filter out time-wasters and pay for the first months of server costs.
- **The phone add-on costs more** because voice AI is charged per minute (Section 13).

Prices are only half the picture. Next is what it costs us to deliver.

---

## 13. Costs

### 13.1 One-off costs (only after the first payment)
| Item | Cost | When |
|---|---|---|
| Domain name | About $15–25 a year 💬 (about $10 quoted) | After the first client, or when outreach needs it |
| ABN | $0 (already done) | ✅ |
| Logo and brand | $0 (made ourselves) | Anytime |

### 13.2 Fixed monthly costs (shared across all clients)
| Stage | Clients | Server | Email and tools | **Total a month** |
|---|---|---|---|---|
| Demo | 0 | $0 (free tier) | $0 | **$0** |
| Start | 1–5 | $0–10 | $0 | **$0–10** |
| Growing | 6–15 | $10–20 | $0–10 | **$10–30** |
| Established | 16–40 | $20–60 | $10–30 | **$30–90** |
| Large | 40+ | $60–150 | $30–60 | **$90–210** |

### 13.3 Variable costs (per client, per month)
| Item | Website only | Chatbot + bookings | + Phone add-on |
|---|---|---|---|
| Server share | $1–2 | $1–3 | $1–3 |
| AI usage | $0 | $3–15 | $3–15 |
| SMS / email | $0 | $0–5 | $0–10 |
| Voice minutes + phone number | $0 | $0 | **$20–60+** (varies with call volume) |
| Payment fees (about 2%) | $1 | $2–3 | $4–7 |
| **Total** | **≈ $2–3** | **≈ $6–26** | **≈ $30–95** |

*Third-party prices change and some are in USD. Check them before quoting.*

Now we can see what each client is actually worth.

---

## 14. Profit per client

### 14.1 Monthly profit per client
| Package | Charge a month | Our cost | **Profit** | **Margin** |
|---|---|---|---|---|
| Managed website | $65 | $3 | **$62** | **95%** |
| Chatbot + bookings | $129 | $15 | **$114** | **88%** |
| Chatbot + bookings + 2 small add-ons | $199 | $20 | **$179** | **90%** |
| Chatbot + bookings + phone AI | $279 | $70 | **$209** | **75%** |

### 14.2 Lifetime value (the long-run picture)
| Item | Expected |
|---|---|
| Average monthly profit per client | About $110 |
| Average time a client stays | About 24 months (assuming about 4% leave each month) |
| Setup profit | About $250 |
| **Lifetime profit per client** | **≈ $2,900** |

**What this means:** every client won is worth roughly **$3k over their lifetime**. That's why time spent finding clients (Section 20) pays off.

The money works. The real limit is the owner's time, which is next.

---

## 15. Workload: what the work involves

### 15.1 Types of work
| Work | What it involves | Time (early) | Time (later, with templates) |
|---|---|---|---|
| **Building the platform** | Chatbot, booking, widget, dashboard code | 30–50 hrs once | Small updates |
| **Lead finding** | Running scripts, checking the list | 2 hrs a week | 1 hr a week (automated) |
| **Outreach** | Emails, DMs, follow-ups, replies | 3–4 hrs a week | 2 hrs a week (automated sequences) |
| **Demos and sales calls** | Video calls, visits, proposals | 1–2 hrs per prospect | 1 hr |
| **Client setup** | Collect info, configure the app, install, test | 6–10 hrs per client | 2–4 hrs |
| **Maintenance and support** | Updates, fixes, questions, monthly report | 1–1.5 hrs per client a month | 0.5 hrs |
| **Admin** | Invoices, bookkeeping, tax | 1 hr a week | 1 hr a week |

### 15.2 A typical 12-hour week
| Stage | Building | Sales (leads + outreach + demos) | Client setups | Maintenance | Admin |
|---|---|---|---|---|---|
| Months 1–2 (no clients) | 6 h | 5 h | 0 h | 0 h | 1 h |
| Months 3–6 (2–8 clients) | 2 h | 5 h | 3 h | 1–2 h | 1 h |
| Months 7–12 (10–22 clients) | 1 h | 4 h | 3 h | 3–5 h | 1 h |

### 15.3 Capacity limit
At **10–15 hours a week**, realistic capacity is **about 2–3 new clients a month** and **about 25–30 clients in total** before maintenance takes over. Going beyond that needs **more automation, more hours, or a helper**. This is why the projections slow down later.

Combining prices, costs and time gives us the projections.

---

## 16. Projections

### 16.1 Assumptions
| Assumption | Value |
|---|---|
| Hours | 10–15 a week |
| Average monthly fee | $120 (year 1), rising to $150 (years 2–3) through add-ons |
| Average setup fee | $150 (first 3 clients), then $250 |
| Variable cost | About $12 per client a month |
| Fixed costs | $0 → $10 → $20 → $40 a month as clients grow |
| Clients leaving | About 1 every 3 months in year 1, about 4% a month later |
| Not included | Income tax (set aside about 20–30%), the owner's time cost |

### 16.2 Stage 1: first income (months 1–3)
**Goal:** the first paid client, to prove the model.

| Month | What happens | New clients | Active clients | Income | Costs | **Profit** |
|---|---|---|---|---|---|---|
| 1 | Build demos and lead list, start outreach | 0 | 0 | $0 | $0 | **$0** |
| 2 | First "yes". Setup fee + month 1 paid. | 1 | 1 | $270 | $42 (server, domain, usage) | **$228** |
| 3 | Second client | 1 | 2 | $390 | $34 | **$356** |
| **Total** | | | | **$660** | **$76** | **≈ $584** |

**First income: expected in month 2, about $270.** From then on, each new client adds about $108 a month in profit.

### 16.3 Stage 2: middle stage (months 4–12, expected scenario)
| Month | New | Left | Active | Monthly fees | Setup fees | Costs | **Profit** |
|---|---|---|---|---|---|---|---|
| 4 | 2 | 0 | 4 | $480 | $400 | $58 | **$822** |
| 5 | 2 | 0 | 6 | $720 | $500 | $82 | **$1,138** |
| 6 | 2 | 0 | 8 | $960 | $500 | $106 | **$1,354** |
| 7 | 2 | 0 | 10 | $1,200 | $500 | $140 | **$1,560** |
| 8 | 2 | 1 | 11 | $1,320 | $500 | $152 | **$1,668** |
| 9 | 3 | 0 | 14 | $1,680 | $750 | $188 | **$2,242** |
| 10 | 3 | 0 | 17 | $2,040 | $750 | $244 | **$2,546** |
| 11 | 3 | 1 | 19 | $2,280 | $750 | $268 | **$2,762** |
| 12 | 3 | 0 | 22 | $2,640 | $750 | $304 | **$3,086** |

**Year 1 total (expected):** monthly fees **$13,680** + setup fees **$5,700** − costs **$1,618** = **≈ $17,760 profit** (before tax and about $400 in payment fees).

### 16.4 Year 1 scenarios
| Scenario | What it assumes | Active clients at M6 | Active clients at M12 | Monthly fees at M12 | Year 1 profit (approx.) |
|---|---|---|---|---|---|
| **Slow** | Half the sales rate, 10 hrs a week | 3 | 10 | **$1,200** | **≈ $7,500** |
| **Expected** | As in the table above, 10–15 hrs a week | 8 | 22 | **$2,640** | **≈ $17,700** |
| **Strong** | Lead engine works well, 15–20 hrs a week | 12 | 35 | **$4,500** | **≈ $30,000** |

### 16.5 Stage 3: long run (years 2–3)
| | Year 2 (end) | Year 3 (end) |
|---|---|---|
| Active clients | 35–45 | 50–60 |
| Average monthly fee (with add-ons) | $140 | $150 |
| **Monthly fees** | **$4,900–6,300** | **$7,500–9,000** |
| Costs (about 12–15%) | $650–900 | $1,000–1,300 |
| **Monthly profit** | **≈ $4,200–5,400** | **≈ $6,500–7,700** |
| Needs | Strong automation, templates, possibly 20 hrs a week | A part-time helper, or partners reselling our platform (Section 8.3) |

### 16.6 Milestones
| Milestone | Expected timing | What it means |
|---|---|---|
| First payment | Month 2 | The model is proven |
| $500 a month recurring | Month 4–5 | Covers small personal costs |
| $1,000 a month recurring | Month 6–7 | A meaningful side income |
| $2,500 a month recurring | Month 12 | Close to part-time wage level |
| $5,000+ a month recurring | Year 2–3 | Full-time-level income |

The numbers lead to the key question: is it worth doing?

---

## 17. Is it profitable? Verdict and decision guide

### 17.1 Verdict
| Test | Result |
|---|---|
| Upfront cost | ✅ $0 |
| Running costs | ✅ Very low ($0–40 a month fixed, about 10–15% of income later) |
| Margin | ✅ 75–95% per client |
| Recurring income | ✅ Monthly retainers |
| Short-term income | ⚠️ Small. About $600 in the first 3 months. |
| Long-term income | ✅ $5k–9k a month possible by years 2–3 |
| Main risk | ⚠️ **Sales.** Without steady new clients, growth stalls. |
| Second risk | ⚠️ **Time.** Above about 25–30 clients, more hours or automation are needed. |

**Overall: profitable, with low risk and slow-to-medium growth.** You won't get rich quickly, but it's a real recurring business that costs almost nothing to start, and it gets more profitable as it grows.

### 17.2 Decision checkpoints
| When | Check | If yes | If no |
|---|---|---|---|
| Week 4 | Demos working, 200+ leads listed, outreach started? | Continue | Fix the setup first |
| Week 8 | 100+ contacts made, 3+ demos booked? | Continue | Change the message or offer, or try the other niche |
| Month 3 | At least 1 paying client? | Continue and template the setup | Re-check the niche, price and outreach. Consider the cash options in Section 18. |
| Month 6 | 5+ clients, $600+ a month? | Add add-ons, scale outreach | Narrow to the best niche or app |
| Month 12 | 15+ clients? | Plan helpers and white-label partners | Keep it as a steady side income, or pivot |

### 17.3 Which niche to keep (after the first 100 contacts each)
Compare **reply rate, demo rate, close rate and average fee**. Keep the niche that gives the **highest income per hour of sales**.

There are other ways to use the same skills, which the next section compares.

---

## 18. Other options compared

| Option | Upfront | Margin | Recurring? | Speed to first $ | Long-run potential | Rating | Verdict |
|---|---|---|---|---|---|---|---|
| **Managed tech services (this plan)** | $0 | 75–95% | ✅ | 1–2 months | High | ★★★★★ | ✅ **Main plan** |
| Freelance projects (Upwork, local one-off builds) | $0 | High | ❌ | Weeks | Medium | ★★★★☆ | ✅ **Useful for quick cash**. Upsell hosting after. |
| Bug bounty / security work | $0 | High | ❌ | Unpredictable | Medium | ★★★☆☆ | ⚠️ Keep as a side activity. Add security checks as an add-on. |
| Micro-SaaS (one product for one niche) | $0 | Very high | ✅ | 6+ months | Very high | ★★★☆☆ | ⚠️ **Later**, grown from a successful app |
| Hosting resale (plain) | Low | Low | ✅ | Months | Low | ★★☆☆☆ | ❌ Commodity |
| White-label platform resale (e.g. GoHighLevel) | $100–700 a month | Medium | ✅ | 1–2 months | Medium | ★★☆☆☆ | ❌ Needs money upfront |
| Selling templates / digital products | $0 | High | ❌ | Slow | Low–medium | ★★☆☆☆ | ⚠️ Optional extra |
| Buying or reselling GPUs or servers | High | Thin | ✅ | Slow | Low | ★☆☆☆☆ | ❌ Avoid |

**Best combination:** managed services as the core, plus **freelance one-off jobs if quick cash is needed** (turn each one into a hosted retainer client), plus **security checks** as a unique add-on using the owner's background.

The plan is decided. The rest of the document covers the tools and steps to carry it out.

---

# Part E: Doing it

## 19. Tools and sources, rated

**Why free first:** the owner has no money, so everything runs on **free tiers, trial credits or open source** until a client pays. Paid tools are switched on only when a client's fee covers them. *Free tiers and prices change, so check before relying on them.*

### 19.1 Servers and hosting
| Source | What it is | Free / cost | Best for | Rating | Verdict |
|---|---|---|---|---|---|
| **Oracle Cloud Always Free** | Free ARM servers (up to about 4 CPU / 24 GB in total) | Free | Main server at the start | ★★★★★ | ✅ Best free option. Sign-up and capacity can be fiddly. |
| **BinaryLane** | Australian VPS provider (Sydney) | From about $4–8 a month | First paid server, with low latency for AU clients | ★★★★★ | ✅ Best paid option in Australia |
| **Hetzner** | Very cheap European VPS (also has Singapore) | From about $6–10 a month | Best price for performance | ★★★★☆ | ✅ Paid option. Further from Australia. |
| **Vultr** | VPS with Sydney and Melbourne regions | From about $4–9 a month | AU-hosted paid option | ★★★★☆ | ✅ |
| **DigitalOcean** | VPS with a Sydney region | From about $6–9 a month | Easy to use | ★★★★☆ | ✅ |
| AWS Lightsail | Amazon's simple VPS, Sydney region | From about $5–8 a month | If AWS is needed | ★★★☆☆ | ⚠️ Fine, but more complex |
| Google Cloud free tier | 1 small free server (US only) | Free | Small background jobs | ★★☆☆☆ | ⚠️ Too small, and in the US |
| **Cloudflare Pages / Workers** | Free hosting for sites and small code | Free | Websites, chat widget, demos | ★★★★★ | ✅ |
| Vercel / Netlify | Free website hosting | Free hobby tier | Websites, demos | ★★★★☆ | ✅ (Vercel's free tier is non-commercial, so check) |
| Render | Free app hosting (sleeps when idle) | Free | Quick demos | ★★★☆☆ | ⚠️ Demos only |
| Local machine + tunnel (Cloudflare Tunnel, ngrok) | Run on your own computer, reachable online | Free | Development and testing | ★★★★☆ | ✅ Testing only |
| Reseller hosting (ResellerClub, Hostinger, cPanel) | Shared hosting to resell | About $15–40 a month | — | ★★☆☆☆ | ❌ A VPS is better value |

### 19.2 Apps (open source we can host)
| Source | What it is | Cost | Rating | Verdict |
|---|---|---|---|---|
| **n8n** | Automation workflows | Free self-hosted | ★★★★★ | ✅ Core tool for automations |
| **Cal.com** | Booking system | Free self-hosted | ★★★★☆ | ✅ Booking app option |
| Easy!Appointments | Simple booking system | Free | ★★★☆☆ | ⚠️ Simpler alternative |
| **WordPress** | Websites | Free | ★★★★☆ | ✅ Managed websites |
| Static sites (Astro, plain HTML) | Fast, simple websites | Free | ★★★★★ | ✅ Cheapest to host |
| Uptime Kuma | Uptime monitoring | Free | ★★★★☆ | ✅ Monitor client systems |
| Umami / Plausible | Website analytics | Free self-hosted | ★★★☆☆ | ⚠️ Extra |
| Nextcloud | File sharing and office | Free | ★★★☆☆ | ⚠️ Niche demand |
| Docker | Packages apps so they're easy to move | Free | ★★★★★ | ✅ Use for everything |

### 19.3 AI and voice
| Source | What it is | Free / cost | Rating | Verdict |
|---|---|---|---|---|
| Google Gemini API | AI model API | Free tier with limits | ★★★★☆ | ✅ Demos and low volume |
| Groq | Fast open-model API | Free tier | ★★★★☆ | ✅ Cheap, fast replies |
| OpenRouter | Access to many models | Some free models | ★★★☆☆ | ⚠️ Testing |
| Anthropic Claude / OpenAI | Top-quality AI APIs | Pay per use (cents per chat) | ★★★★★ | ✅ Production, once clients pay |
| **Vapi** | Voice AI platform | Free starting credit, then per minute | ★★★★☆ | ✅ Phone add-on (Demo 2 uses it) |
| Retell AI | Voice AI platform | Free credit, then per minute | ★★★★☆ | ✅ Alternative to Vapi |
| Twilio | Phone numbers and SMS | Trial credit, then pay per use | ★★★☆☆ | ⚠️ Once clients pay |

### 19.4 Data, email, payments
| Source | What it is | Free / cost | Rating | Verdict |
|---|---|---|---|---|
| SQLite | Simple database file | Free | ★★★★★ | ✅ Start here |
| Supabase | Hosted Postgres database + logins | Free tier | ★★★★☆ | ✅ When it grows |
| Neon | Hosted Postgres | Free tier | ★★★☆☆ | ⚠️ Alternative |
| Brevo | Email sending and sequences | About 300 emails a day free | ★★★★☆ | ✅ Outreach |
| Resend | Email sending from code | About 3,000 a month free | ★★★★☆ | ✅ Notifications |
| Zoho Mail | Business email on your domain | Free plan (limits) | ★★★☆☆ | ✅ Professional email |
| **Stripe** | Card payments, invoices, subscriptions | Free to set up, fee per payment | ★★★★★ | ✅ Monthly billing |
| PayPal | Payments and invoices | Fee per payment | ★★★☆☆ | ⚠️ Backup |

### 19.5 Leads and tracking
| Source | What it is | Free / cost | Rating | Verdict |
|---|---|---|---|---|
| **OpenStreetMap (Overpass API)** | Free map data: business names, types, addresses, some websites | Free | ★★★★☆ | ✅ Main free source |
| **Google Places API** | Official Google Maps data: websites, phone, ratings, reviews | Free monthly allowance, then paid | ★★★★☆ | ✅ Official API only. Don't scrape Google Maps. |
| **ABN Lookup web services** | Government business register | Free (register for a GUID) | ★★★★☆ | ✅ Check businesses are active |
| Directories (Healthdirect, Yellow Pages, True Local) | Business listings | Free to browse | ★★★☆☆ | ⚠️ Check their terms. Use lightly or by hand. |
| Hunter.io | Email finder | About 25 searches a month free | ★★☆☆☆ | ⚠️ Top leads only |
| **Google Sheets** | Lead tracker | Free | ★★★★★ | ✅ Start here |
| Airtable / Notion | Tracker with nicer views | Free tier | ★★★☆☆ | ⚠️ Optional |
| GitHub Actions | Runs scripts on a schedule | Free tier | ★★★★☆ | ✅ Automate lead collection |

With the tools chosen, the next step is getting clients, which is the biggest risk named in Section 17.

---

## 20. Getting clients: the automated lead system

**Goal:** find local businesses automatically, profile what they already have, score them, find a public contact, and run tracked email outreach with demos. The owner then spends their time on replies and demos, not searching.

### 20.1 Pipeline
```
1 Collect ─▶ 2 Visit website ─▶ 3 Detect tools ─▶ 4 Profile & score ─▶ 5 Find contact ─▶ 6 Tracker ─▶ 7 Emails & demos
```

### 20.2 Step 1: collect
Pull businesses by category (barbers, salons, physio, chiro and so on) around Newcastle, then Lake Macquarie and Maitland, from **OpenStreetMap** and the **Google Places API**. Check they're active with **ABN Lookup**.

### 20.3 Steps 2–3: visit the website and detect tools
Fetch the homepage and contact or booking pages, and look for code signatures:

| Looking for | Examples detected |
|---|---|
| Chat widgets | Intercom, Tidio, Tawk.to, Crisp, LiveChat, Drift, HubSpot, Facebook chat, Podium |
| AI assistants | Chatbase, Voiceflow, Botpress, custom AI widgets |
| Booking (health) | HotDoc, HealthEngine, Cliniko, Nookal, Halaxy |
| Booking (beauty and general) | Fresha, Timely, Square, Kitomba, Setmore, Calendly |
| Website platform | Wix, Squarespace, WordPress, Shopify (tells us how easy the install is) |
| Gaps | No online booking · contact form only · slow or not mobile-friendly · no SSL · outdated |

### 20.4 Step 4: profile the client and any competitor
**Client profile:** niche, size (staff, reviews), opening hours, how customers contact them, existing tools, website quality, gaps.

**Competitor profile** (if they already have a tool):
| Question | Our angle |
|---|---|
| What tool is it? | Know what we're up against |
| Human live chat, or AI? | Human chat with nobody after hours → we add 24/7 AI |
| Can it book, or only take messages? | Booking is an upgrade |
| Does it know their services and prices? | Generic bots perform badly → ours is trained on their business |
| Phone covered? | Phone add-on |
| **Pitch** | "Keep your [tool]. We add [missing piece]." |

**Lead score (0–100):**
| Signal | Points |
|---|---|
| Booking-based niche | +25 |
| No chat or AI on the site | +20 |
| Human-only or basic chat | +10 |
| Booking system but no assistant | +10 |
| Phone or form is the only contact option | +10 |
| 20–300 Google reviews (established, independent) | +10 |
| Independent, not a chain | +10 |
| Public email found | +5 |
| Already has an advanced AI receptionist | −30 |

### 20.5 Step 5: find contacts (legally)
✅ Emails published on their website, Google listing or Facebook page · ✅ owner name from their About page · ⚠️ email finders only for top leads · ❌ no guessed emails, no bought lists.

### 20.6 Step 6: tracker
**Fields:** `business, niche, suburb, phone, website, public_email, contact_name, source, rating, reviews, platform, chat_tool, ai_tool, booking_tool, gaps, pitch_angle, score, status, last_contacted, next_action, next_action_date, demo_url, notes, unsubscribed`

| Status | Next automatic step |
|---|---|
| `new` | Run the profiler |
| `profiled` | If score ≥ 60, queue it |
| `queued` | Send email 1 |
| `contacted` | Email 2 after 3–4 days if no reply |
| `followed_up` | Email 3 after 7 days |
| `replied` | **The owner takes over** |
| `demo_booked` | Reminder the day before |
| `demo_done` | Send proposal |
| `won` | Start onboarding |
| `lost` / `not_now` | Recheck in 3–6 months |
| `unsubscribed` | **Never contact again** |

### 20.7 Step 7: emails and personalised demos
| Email | When | Content |
|---|---|---|
| 1 | Day 0 | Mentions something specific about their business, a one-line benefit, **a demo made for their business**, and an unsubscribe line |
| 2 | Day 3–4 | A short example of the problem we solve in their niche |
| 3 | Day 10 | Polite last note |

- **Personalised demo:** generate a demo page for each top lead using their name, services and hours (built from `chat-demo.html`).
- **Volume:** 20–30 emails a day from a proper domain.
- **Tools:** Google Sheet + Brevo/Resend + n8n on our server.

### 20.8 Expected funnel (for planning)
| Stage | Rate (💡 estimate) | From 300 leads |
|---|---|---|
| Emailed | 100% | 300 |
| Replied | 5–10% | 15–30 |
| Demo held | 50% of replies | 8–15 |
| Won | 25–35% of demos | **2–5 clients** |

That's roughly **100–150 contacts per client**, which matches the expected projection of 1–3 new clients a month.

The demos are the other key sales tool. Here's what exists today.

---

## 21. What's been built: demos and dry test run

### 21.1 Built so far
| File | What it is | Status |
|---|---|---|
| `chat-demo.html` | **Demo 1.** Chat booking demo for the made-up "Foreshore Fade Co." barber, with an owner diary. Scripted replies, not real AI. Saves in the browser only. | ✅ Works. For showing the flow. |
| `server.js` | **Demo 2 backend.** Node.js, no packages. Check-times and book tools, plus a `/bookings` diary page. Used by the **phone add-on** through Vapi, and reusable for the chatbot. | ✅ Tested locally |
| `assistant-prompt.md` | AI voice instructions and first message for Vapi | ⚠️ Not tested on a live call |
| `tools.json` | Vapi tool definitions (server URL to fill in) | ⚠️ Not tested on a live call |
| `README.md` | Setup steps | ✅ |

**Local test results:** free times ✅ · booking ✅ · overlapping booking refused ✅ · closed day handled ✅ · live voice call ❌ not yet done.

**Technical notes:** `POST /vapi/tools` uses Vapi's `toolCallList` format · dates `YYYY-MM-DD`, times 24-hour `HH:MM`, timezone `Australia/Sydney` · optional `TOOL_SECRET` checked against the `x-vapi-secret` header · data in `bookings.json` (not durable on free hosting) · shop settings in `SHOP`.

### 21.2 Not built yet
- **The real AI chatbot** (the starting offer)
- The embeddable chat widget and multi-client setup
- The lead system (Section 20)
- A clinic demo

### 21.3 Dry test run ($0)
**Purpose:** prove the flow works before contacting any business.

| Step | Task | Done when |
|---|---|---|
| 1 | Open `chat-demo.html` and make bookings | The flow is clear on desktop and phone |
| 2 | Run `node server.js` and open `/bookings` | The diary loads |
| 3 | Make it reachable online (tunnel or Render) | A public URL works |
| 4 | Set up Vapi with the prompt and tools, and test by browser "Talk" | **5 voice bookings in a row are correct** |
| 5 | Try edge cases: closed day, full day, odd questions | Handled sensibly |
| 6 | Record short demo videos | Ready for outreach |

The roadmap below takes this from demo to business.

---

## 22. Roadmap

| Phase | Timing | Goal | Key work | Done when |
|---|---|---|---|---|
| **0. Dry test run** | Week 1 | Demos proven | Section 21.3 | Videos recorded |
| **1. Core platform MVP** | Weeks 1–4 | Real AI chatbot for many clients | AI chat endpoint (reusing `server.js` tools), embed widget, per-client settings, lead notifications by email, simple owner page, deployed on Oracle Free | Works on a test site |
| **2. Lead system** | Weeks 2–4 | 200+ scored leads | Collector, profiler, scoring, Google Sheet tracker | 100+ salons and 100+ clinics listed |
| **3. Outreach and first clients** | Weeks 4–10 | First 1–3 paying clients | Domain + email, 3-email sequence, personalised demos, demo calls and visits | **First payment** |
| **4. Deliver and template** | Months 3–6 | Setup in under 4 hours | Onboarding checklist, templates, backups, monthly reports, testimonials | 5–8 clients |
| **5. Expand the menu** | Months 6–12 | Higher fee per client | Add-ons: reminders, reviews, phone AI, websites, automations, security checks | Average fee $150 or more |
| **6. Scale** | Year 2+ | 40+ clients | Paid VPS, integrations (Cliniko, Fresha, Calendar), more niches and areas, white-label partners, possible helper | $5k+ a month |

---

## 23. Scope

| In scope now | Later | Out of scope |
|---|---|---|
| Managed tech services model | Micro-SaaS | Buying hardware or GPUs |
| AI chatbot + bookings as the starting offer | White-label partner programme | Plain hosting resale |
| Barbers & salons + allied health clinics | Trades, dental, vets, gyms | Paid ads before revenue |
| Our own platform on free servers | Paid VPS upgrades | Paid white-label platforms before revenue |
| Phone AI as an add-on | Deep integrations | Any medical advice or triage |
| Automated lead system + email outreach | Helpers or contractors | Areas outside Newcastle & surrounds (for now) |

---

## 24. Risks and compliance

### 24.1 Australian rules
| Area | What it means for us |
|---|---|
| **Spam Act 2003** | Only email businesses whose address is **published** and where the message is **relevant to their work** (inferred consent). Every email must identify us and include a working **unsubscribe**, honoured within 5 business days. No bought or guessed lists. |
| **Privacy Act** | Collect minimal data, store it securely, have a privacy policy. **Health information is sensitive**, so take extra care with clinics. |
| **Platform terms** | Use official APIs (Google Places, Overpass, ABN Lookup). Don't scrape sites that forbid it. |
| **AI disclosure** | Tell customers they're talking to an AI assistant |
| **Do Not Call Register** | Check before any cold calling |
| **Tax** | Register for GST once turnover reaches $75k. Set aside 20–30% of profit for income tax. |

### 24.2 Business risks
| Risk | Likelihood | Mitigation |
|---|---|---|
| Not enough clients | Medium–high | Lead system, personalised demos, two niches tested |
| Owner time runs short | Medium | Templates, automation, capacity plan (Section 15) |
| Free tiers change | Medium | Docker setup, ready to move to a cheap VPS paid by clients |
| AI mistakes | Medium | Strict prompts, tool-only availability, human hand-off |
| Clients leave | Low–medium | Bundles, monthly reports showing value |
| Usage costs grow | Low | Usage caps, pricing per package |

---

## 25. Handover rules, open questions, next actions

### 25.1 Rules for whoever takes over
1. **$0 upfront, minimal running costs, maximum margin.** No paid tool before a client pays for it.
2. **The business is managed tech services** (server + app + maintenance). The chatbot is the **starting offer** for the first niche, not the whole business.
3. **The phone AI is an add-on.**
4. Run **barbers & salons and allied health clinics in parallel**, then compare (Section 17.3).
5. **Plain English** with the owner.
6. Stay within Australian law (Section 24).
7. Label figures: 💬 discussed or 💡 suggested.
8. Keep code simple and portable (Node.js, Docker, few packages).

### 25.2 Open questions for the owner
| # | Question |
|---|---|
| 1 | Brand name and domain? |
| 2 | Confirm the suggested 💡 prices in Section 12, or adjust them |
| 3 | Contract terms: minimum term, notice period, support hours? |
| 4 | Personal income target, to set a "success" line in Section 17? |

### 25.3 Next actions
| # | Action | Who |
|---|---|---|
| 1 | Dry test run of both demos (Section 21.3) | Owner |
| 2 | Build the lead system: collector + profiler + Google Sheet | Owner + AI |
| 3 | Build the real AI chatbot MVP + widget | Owner + AI |
| 4 | Make a clinic demo alongside the barber demo | Owner + AI |
| 5 | Choose a brand name. Prepare the email domain for outreach. | Owner |
