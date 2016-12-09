# Design of RESTful API for NFFG verifier

## Conceptual structure of the resources

### verifier

The root resource. Includes nffgs

TODO

### nffgs

The collection of nffg elements.

TODO

### nffg

Inlcudes info about the nffg: name, last update, nodes, links, policies??

TODO

### policies

The collection of policies.

TODO

### policy

TODO

### result

TODO

## Mapping of the resources to URLs

TODO

## Operations by resource

Methods summary:
```text
/                                                      GET, DELETE
    nffgs/                                             GET, POST
          {nffg_id}/                                   GET, DELETE
                   policies/                           GET, POST
                            {policy_id}/               GET, DELETE, PUT/PATCH
                                        result/        GET
                   online_result/                      POST (because need body request)
    policies/                                          GET (flat view. readonly??)
          {policy_id}/                                 GET ??
                   result/                             GET ??
```

### `/` verifier root

This is the root resource, also the entry point. Returns all the informations about NFFGs and policies.

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | verifier      | get the informations (pagination)  |
| DELETE | -            | OK            | delete all the informations |

### `/nffgs`

NFFGs collection

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | nffgs         | get the collection of NFFGs (pagination) |
| POST   | nffg_req     | nffg          | create a new NFFG      | 4xx wrong request

TODO: the id of nffg (named entities) is the name? PROs: same type in POST request and in response, no duplicated unique attribute, no need to store name mappings to ids. CONs: the name is always a valid URL?

### `/nffgs/{nffg_id}`

A single nffg identified by its id

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | nffg          | get the NFFG           | 404: wrong nffg_id
| DELETE | -            | OK            | delete the NFFG        | 404: wrong nffg_id

### `/nffgs/{nffg_id}/policies`

Policies collection for this NFFG

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | policies      | get the collection of policies (pagination)  | 404: wrong nffg_id
| POST   | policy_req   | policy        | create a new policy    | 404: wrong nffg_id, 4xx: wrong request

### `/nffgs/{nffg_id}/policies/{policy_id}`

A policy identified by its id

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | policy        | get the policy         | 404: wrong id
| DELETE | -            | OK            | delete the policy      | 404: wrong id

### `/nffgs/{nffg_id}/policies/{policy_id}/result`

The result of this policy

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| GET    | -            | result        | the verification of policy is performed and the result is both stored and returned | 404: wrong id

### `/nffgs/{nffg_id}/online_result`

Verification endpoint for client policies, not stored on the service

| method | request type | response type | explaination           | errors
| ------ | ------------ | ------------- | ------------           | ------
| POST   | policy_req   | policy/result?? | get the result for this policy | 404: wrong id, 4xx: wrong request

### `/policies`

This is a flat view on the policies. But should be readonly: POST requests should also contain the NFFG name/id and another type should be created.

??? TODO: replication of subtree about policies
