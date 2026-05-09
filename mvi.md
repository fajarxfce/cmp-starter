# MVI

Each screen owns:

- `Intent`: user or lifecycle actions.
- `State`: immutable UI snapshot.
- `Effect`: one-time events.
- `PartialChange`: state transition inputs.
- `Reducer`: pure state transformation.
- `ViewModel`: side-effect coordinator.

Reducers are pure, small, and tested without coroutine or platform dependencies.
