# booking
quick spring rest controller app with a bit of hexagonal architecture
domain exposes 2 ports for rest adapter and repository/jdbc adapter.
Additional grpc or graphql adapter wouldn't have impacted the domain or repository adapter,
layers enforced by package convention and ArchUnit.

using integrity constraint on 'dates' for preventing to book the same day multiple time
