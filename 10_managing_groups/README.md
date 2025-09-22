# Managing Groups

This directory contains comprehensive CRUD examples for managing groups and their relationships with accounts and roles using the StrongDM Java SDK.

## Examples

### Groups CRUD
[`groups_crud`](./groups_crud) - Complete CRUD operations for Groups
- **Create**: Create new groups
- **Read**: List and filter groups
- **Update**: Modify group properties
- **Delete**: Remove groups
- Includes resource cleanup after demonstration

### AccountsGroups CRUD
[`accounts_groups_crud`](./accounts_groups_crud) - Complete CRUD operations for AccountsGroups
- **Create**: Link accounts (users) to groups
- **Read**: List and filter account-group relationships
- **Delete**: Remove account-group relationships
- Creates prerequisite accounts and groups
- Includes complete resource cleanup

### GroupsRoles CRUD
[`groups_roles_crud`](./groups_roles_crud) - Complete CRUD operations for GroupsRoles
- **Create**: Link groups to roles
- **Read**: List and filter group-role relationships by group or role
- **Delete**: Remove group-role relationships
- Creates prerequisite groups and roles
- Includes complete resource cleanup

## Prerequisites

All examples require:
- StrongDM API keys set as environment variables:
  - `SDM_API_ACCESS_KEY`
  - `SDM_API_SECRET_KEY`
- Java 11 or later
- Gradle 6.0 or later

## Usage

Each example can be run independently and demonstrates the complete lifecycle:

```bash
# Groups CRUD example
cd groups_crud
gradle run

# AccountsGroups CRUD example
cd accounts_groups_crud
gradle run

# GroupsRoles CRUD example
cd groups_roles_crud
gradle run
```

## Features

- **Complete CRUD Operations**: Each example demonstrates Create, Read, Update (where applicable), and Delete operations
- **Comprehensive Listing**: Shows how to list all resources and filter by specific criteria
- **Resource Management**: All examples create test resources, demonstrate operations, and clean up afterwards
- **Real-world Usage**: Examples show practical patterns for managing groups in production environments
- **Error Handling**: Proper error handling and logging throughout
- **Resource Dependencies**: Examples that require prerequisite resources (accounts, groups, roles) create them automatically

## Current Status

**Important Note**: The Groups functionality is not yet available in the published StrongDM Java SDK v15.0.0. The current examples are working **demos** that:

1. **Show the exact structure** that the full examples will use once Groups support is available
2. **Compile and run successfully** with the current SDK version
3. **Demonstrate equivalent patterns** using available functionality (accounts, roles)

### Demo Examples (Available Now)

- `groups_crud` → Runs `GroupsCrudDemo` - Shows Groups CRUD structure using Role CRUD as example
- `accounts_groups_crud` → Runs `AccountsGroupsCrudDemo` - Shows AccountGroups structure using Account operations
- `groups_roles_crud` → Runs `GroupsRolesCrudDemo` - Shows GroupsRoles structure using Role management

### Full Examples (Future)

Once Groups support is added to the published Java SDK, the examples will be updated to use the actual Groups APIs exactly matching the Go SDK examples.

## Running the Current Demos

```bash
# Each demo compiles and runs with current SDK
cd groups_crud
gradle run

cd accounts_groups_crud
gradle run

cd groups_roles_crud
gradle run
```