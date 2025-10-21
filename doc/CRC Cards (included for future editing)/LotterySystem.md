# LOTTERY_SERVICE

| Responsibilities                                                                          | Collaborators           |
| ----------------------------------------------------------------------------------------- | ----------------------- |
| Select *n* random ==Entrant==s from Event waiting list                                    | ==Event==               |
| Move selected ==Entrant== from "waiting" to "attendee" list                               | ==waitingList==         |
| Select random ==Entrant== from ==waitingList== as a replacement for cancelled ==Entrant== | ==attendeeList==        |
| Trigger notifications for "winning" and "losing" lottery                                  | ==notificationService== |
