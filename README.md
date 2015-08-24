ATTENTION
=========

**There is a Bug to Work Around**

Example:

ID  | Action | Type | Number | Time
--- | ------ | ---- | ------ | ----
A1  | R      | F    | 2      | 0
A2  | R      | B    | 10     | 0
A3  | R      | F    | 10     | 1
A1  | C      | F    | 1      | 4
A2  | C      | B    | 2      | 14

Becomes:

ID  | Action | Type | Number | Time
--- | ------ | ---- | ------ | ----
A1  | R      | F    | 2      | 0
A1  | C      | F    | 1      | 4
A2  | R      | B    | 10     | 0
A2  | C      | B    | 2      | 14
A3  | R      | F    | 10     | 1

But Should Be:

ID  | Action | Type | Number | Time
--- | ------ | ---- | ------ | ----
A1  | R      | F    | 2      | 0
A2  | R      | B    | 10     | 0
A1  | C      | F    | 1      | 4
A2  | C      | B    | 2      | 14
A3  | R      | F    | 10     | 1

To solve this issue, I'll give you two
queues: One for Reservations and one for
Cancellations. When selecting which request
to process next, check the next cancellation
and find out what the time was of that agent's
last reservation. If the next reservation's
time is greater, then process the cancellation.
Otherwise, process the reservation first.

If necessary, I _could_ implement this
dual-queue thing inside a single queue, but
then you're left with almost no work.
