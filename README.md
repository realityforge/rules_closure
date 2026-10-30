# Peter Donald's rules_closure fork

This fork collects independently reviewable changes for
[bazelbuild/rules_closure](https://github.com/bazelbuild/rules_closure).
The default branch contains only these maintenance instructions, with independent history.
`upstream` contains unmodified upstream history. `feature/*` branches retain individual
changes, merged through pull requests into `integration`. Refreshes rewrite integration
history; consumers pin its exact tested commit. Permanent snapshot tags retain published commits.

## Prompt for a refresh and rollout

Supply the consumer repository scope privately when invoking this prompt. Consumer names,
URLs, local paths, application fixtures and provenance belong in private task records.

```text
Fork: https://github.com/realityforge/rules_closure
Upstream: https://github.com/bazelbuild/rules_closure

Refresh this fork and roll its tested integration commit through the privately supplied
consumer scope. This authorizes implementation, tests, commits, pushes, and creation and
merge of pull requests inside the fork and scoped consumers after their required checks
and reviews. Preserve existing upstream submissions, unrelated refs and user changes.
Upstream pull requests require separate authority.

1. Read repository instructions and later user direction. Work on one repository at a time,
   with one owner for its graph and serialized local builds. Move to another repository
   after the current change reaches an unprotected default branch, its PR merges, or its
   PR is waiting for CI. Pending CI gates only that repository. Use the established PR
   monitoring workflow for waiting PRs; repairs must respect the same serial-work rule.

2. Discover consumers within the privately authorized account/repository scope. Enumerate
   accessible repositories rather than relying on search indexes or local checkouts alone.
   Inspect current default-branch dependency declarations, build hooks, generated consumer
   checks, active PRs and relevant worktrees. Follow source dependencies and vendored mirrors
   to their canonical owners. Distinguish direct consumers, transitive requirements, tooling
   references and archived examples. Record exact revisions, local changes, patch order,
   coverage gaps and every distinct patch hunk privately.

3. Verify fork metadata and remotes, discover upstream's default branch, and freeze its
   current commit as the batch baseline. Create or fast-forward upstream to that exact
   commit; investigate divergence before changing it. Inspect feature refs, integration's
   merge graph and open/merged PRs. Recover missing retained feature refs from verified PR
   heads or merge parents. Keep automatic feature-branch deletion disabled.

4. Give every patch hunk a disposition: retain as a named feature; already satisfied upstream
   with a containing revision and behavioral evidence; replace with supported configuration;
   obsolete with evidence; or unresolved with its affected capability. Compare variants and
   partition by independently useful behavior. Keep application policy and data downstream.
   Each feature starts directly from the baseline. Keep inseparable behavior together.

5. Refresh existing integration and its integrated feature refs as one graph. Use
   --rebase-merges in a controlled clone, preserving feature boundaries and nested merges.
   When using --update-refs, audit every affected ref and branches checked out in worktrees;
   verify the intended feature heads afterward. Retire an upstreamed feature only after its
   requirement is verified on the baseline. Retain the original merged PRs as provenance.
   Avoid independently rebasing integration and features and then merging equivalent commits.
   Validate and publish this refreshed graph with exact leases before opening new feature PRs,
   so their remote integration base already contains the frozen baseline.

6. For new behavior, reproduce the problem and add focused, self-contained regression
   coverage. Verify each feature independently. Push feature/<descriptive-name> and open
   a PR with integration as base. Describe the general problem, resulting behavior,
   non-obvious choices and meaningful validation for an upstream maintainer. Keep consumer
   identities out of public code, commits, PRs and this README. Inspect the complete diff,
   satisfy required review/checks, and merge with a merge commit. Retain the feature branch.
   Fetch and reconcile each remote merge before testing the combined integration graph.

7. Test the combined integration commit and distinguish baseline failures from regressions.
   Verify every retained requirement and patch disposition. Test unreleased APIs against the
   actual source commits that provide them.

8. Fetch upstream again before final publication; refresh and repeat affected validation if
   it advanced. Publish rewritten integration and feature refs with exact
   --force-with-lease=refs/heads/<branch>:<verified-remote-SHA> checks. Reconcile a failed
   lease before retrying. Permanently retain a unique annotated snapshot tag for every tested,
   published integration tip. Report that immutable commit as the consumer pin.

9. After publishing this tested snapshot, refresh any dependent fork that also needs work.
   Give that bounded refresh the published immutable dependency SHA and request a tested SHA
   without starting another rollout. Preserve existing work and reuse suitable in-flight
   changes with attribution. Finish that repository before proceeding.

   Migrate scoped consumers serially in dependency order using the frozen tested fork SHAs.
   Update pins, verified archive checksums, dependency locks, callers and obsolete patches.
   Preserve required behavior while retiring replaced infrastructure. Deliver shared sources
   before refreshing their vendored mirrors, preserving mirror-local changes. Run each
   repository's focused checks and required delivery gates; merge independently when ready.
   Long CI runs must not block unrelated consumer delivery. Monitor pending PRs and continue
   their scoped maintenance within the accepted authority and serial-work restriction.

10. Keep inventories and detailed provenance private. Update these instructions when accepted
    workflow changes require it. Remove temporary scaffolding after preserving useful evidence.
    Report exact baselines, feature PRs, tested snapshot commits, requirement dispositions,
    consumer delivery states, pending CI and unavailable verification. Completion requires
    every scoped consumer to be delivered or an explicit, evidenced blocker to be reported.
```
