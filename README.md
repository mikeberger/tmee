# tmee - TicketMaven Web version
This project is a web version of a closed-source Ticketing Program called TicketMaven. The closed source version is a 13+ year old Java/Swing desktop app
that automates the ticketing process used by some private communities with their own theaters.

**The web version is still incomplete and lacks some of the in-depth features of the desktop version.**

A lot of features are based on the desktop version. The desktop help is [here](https://mikeberger.github.io/tmee/desktop_help/)
The desktop help can serve as a design reference of sorts.

The web version is multi-tenant, meaning that a single running deployment can simulataneously support multiple communities. Each community would not have permission to see the data from any other, nor would a community be aware of the existence of the other communities.
