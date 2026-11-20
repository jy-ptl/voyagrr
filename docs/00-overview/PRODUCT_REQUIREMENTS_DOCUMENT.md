# Voyagrr — Product Requirements Document

## 1. Purpose

The purpose of this document is to define and document the functional and non-functional requirements of Voyagrr.

It serves as a reference for developers, testers and contributors to understand the expected behavior, capabilities and
constraints of the platform.

This document translates the product vision and scope defined in the Project Overview into clear, implementable and
verifiable requirements.

## 2. Product Scope

### 2.1 In Scope

The initial release of Voyagrr (MVP) will cover the following capabilities:

#### User and Access Management

- User authentication and account management.
- Configurable user registration policies.
- Private media libraries and ownership-based access control.
- Selective sharing of media between users.

#### Media Management

- Photo and video uploads and downloads.
- Media browsing and timeline.
- Basic album management.
- Media metadata extraction, including GPS information.
- Thumbnail and preview generation.
- Duplicate media detection.
- Trash, restore and permanent deletion.

#### Sharing

- Internal sharing between Voyagrr users.
- External sharing through secure links.
- Share expiry, revocation and access permissions.
- Optional password protection for external shares.

#### Video Processing

- Original video playback when compatible.
- HLS transcoding when required.

#### Deployment and Administration

- Self-hosted deployment using Docker Compose.
- S3-compatible object storage.
- System administration and user management.
- Background job management and storage statistics.
- Basic operational monitoring and backup procedures.

### 2.2 Out of Scope

The following capabilities are excluded from the initial MVP:

- Automatic Android folder synchronization.
- iOS, web and desktop applications.
- Interactive map-based media exploration.
- Trips and travel timelines.
- Smart albums.
- Social networking features.
- Messaging and audio/video calling.
- AI-powered media recognition and facial recognition.

### 2.3 Future Considerations

The following capabilities may be considered for future releases:

#### Device and Platform Expansion

- Automatic synchronization of selected Android folders.
- iOS, web and desktop applications.

#### Advanced Media Management

- Interactive maps and location-based media exploration.
- Trips and travel timelines.
- Smart albums and advanced media organization.
- Advanced media discovery and search.
- Media statistics and insights.

#### Intelligent Features

- AI-powered media organization.
- Facial recognition and people-based grouping.

#### Social and Communication

- Social and community features.
- Messaging.
- Audio and video calling.

These future capabilities are not commitments for the initial MVP and will be evaluated separately.

## 3. User Roles and Permissions

Voyagrr supports multiple users within a single installation. Each regular user has a private media library. Access to
media owned by other users is controlled through explicit sharing permissions.

The platform separates system administration, media ownership and recipient access.

### 3.1 System Administrator

The System Administrator is responsible for managing and operating the Voyagrr installation.

Permissions:

- Manage user accounts.
- Configure system-level settings.
- Manage background processing jobs.
- View storage usage and system statistics.
- Monitor application health and operational status.

Restrictions:

- Cannot browse users' private media through the application.
- Cannot download users' private media through the application.
- Cannot modify or delete users' media through application interfaces.

System administration and personal media management use separate accounts. An administrator who wants to use Voyagrr for
personal media must have a separate regular user account.

### 3.2 Regular User

A Regular User manages their own personal media library.

Permissions:

- Upload and download personal photos and videos.
- Browse and organize personal media.
- Create and manage albums.
- View and manage media metadata.
- Share individual media items with other Voyagrr users.
- Share albums with other Voyagrr users.
- Configure permissions for internal shares.
- Create and manage external share links.
- Revoke previously created shares.
- Manage personal account settings.

Restrictions:

- Cannot access another user's private media without explicit permission.
- Cannot modify or delete media owned by another user.
- Cannot access system administration functionality.

### 3.3 Internal Recipient

An Internal Recipient is a registered Voyagrr user who has received access to media or albums shared by another user.

Permissions:

- View media explicitly shared with them.
- Access albums shared with them.
- Download shared media when downloading is permitted.
- Access newly added or remaining album content while the dynamic album share is active.

Restrictions:

- Cannot access the owner's entire private media library.
- Cannot modify or delete the owner's original media.
- Cannot manage or revoke shares created by the owner.
- Cannot access resources outside the granted permissions.

### 3.4 External Recipient

An External Recipient is a person who accesses shared content through an external share link without requiring a Voyagrr
account.

Permissions:

- View media included in a valid share link.
- Download shared media when downloading is permitted.

Access restrictions:

- Access is limited to the content included in the share.
- Access must respect the share's expiry and permissions.
- Revoked or expired links must no longer grant access.
- Password-protected shares require successful authentication using the configured share password.

### 3.5 Role and Permission Matrix

| Capability              | System Admin | Owner | Internal Recipient | External Recipient |
|-------------------------|--------------|-------|--------------------|--------------------|
| Manage system settings  | Yes          | No    | No                 | No                 |
| Manage user accounts    | Yes          | No    | No                 | No                 |
| View system statistics  | Yes          | No    | No                 | No                 |
| Upload personal media   | No           | Yes   | No                 | No                 |
| Browse personal media   | No           | Yes   | No                 | No                 |
| Download personal media | No           | Yes   | No                 | No                 |
| Create albums           | No           | Yes   | No                 | No                 |
| Share individual media  | No           | Yes   | No                 | No                 |
| Share albums            | No           | Yes   | No                 | No                 |
| View shared media       | No           | No    | If granted         | If permitted       |
| Download shared media   | No           | No    | If permitted       | If permitted       |
| Revoke own shares       | No           | Yes   | No                 | No                 |

Note: Owner refers to the user who owns the resource being accessed. A regular user can be an owner for their own media
and a recipient for media shared by another user.

### 3.6 Media Ownership and Access Rules

1. **Private by default:** Every uploaded media item belongs to one user and remains private unless explicitly shared.

2. **Ownership enforcement:** Only the owner can modify, delete or manage their original media and albums.

3. **Explicit sharing:** Access to another user's media requires an explicit sharing grant from the owner.

4. **Family privacy:** Membership in the same installation or household does not automatically grant access to another
   user's media.

5. **Sharing restrictions:** Recipients can access only the media and albums included in their sharing permissions.

6. **Ownership preservation:** Sharing media does not transfer ownership to the recipient.

7. **Access revocation:** Owners can revoke sharing permissions previously granted to internal users or external
   recipients.

8. **External link validation:** External share links must respect expiry, revocation, password protection and
   configured permissions.

9. **Administrator isolation:** System administrators cannot browse or download private user media through the Voyagrr
   application.

10. **Infrastructure access limitation:** Application-level access restrictions do not guarantee protection against
    administrators with direct root, database or object-storage access.

### 3.7 Sharing and Access Restrictions

- Internal sharing supports individual media and albums.
- External sharing supports individual media and albums.
- Internal share permissions can be configured as view-only or view-and-download.
- External share permissions can be configured as view-only or view-and-download.
- External shares can have configurable expiry.
- External shares can optionally require a password.
- Owners can revoke internal and external shares.
- Recipients cannot modify or delete the owner's original media.

### 3.8 Shared Album Behavior

Voyagrr uses dynamic sharing for shared albums.

- Recipients can access the album's currently available contents while their sharing permission remains active.
- Media added to a shared album becomes available to existing recipients.
- Media removed from a shared album is no longer accessible through that album share.
- Changes to an album do not transfer ownership of its media.
- Removing a recipient's sharing permission revokes their access to the album.
- Separate sharing grants for individual media remain independent of album sharing.

Revoking access prevents future access through Voyagrr but cannot remove copies of media that recipients have already
downloaded.

## 4. Functional Requirements

This section defines the functional behavior that Voyagrr must provide. Each requirement will have a unique identifier,
description, priority and acceptance criteria.

### 4.1 Authentication and Account Management

#### FR-AUTH-001 — User Login

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow registered users with active accounts to authenticate using either their registered email address
or username and their password.

Usernames must be unique within a Voyagrr installation. Username matching must be case-sensitive.

Upon successful authentication, the system must establish a new authenticated session for the user without terminating
existing sessions on other devices.

**Acceptance Criteria:**

- AC-001: A user can log in using their registered email address and correct password.
- AC-002: A user can log in using their registered username and correct password.
- AC-003: The system rejects incorrect credentials.
- AC-004: The system rejects login attempts for inactive or disabled accounts.
- AC-005: Successful login establishes an authenticated session.
- AC-006: Logging in on another device does not terminate existing active sessions.
- AC-007: Unauthenticated users cannot access protected resources.
- AC-008: The system does not allow duplicate usernames within the same installation.
- AC-009: Username matching is case-sensitive.

#### FR-AUTH-002 — User Logout

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow an authenticated user to log out from
the current device by terminating the current authenticated
session.

Logging out from one device must not affect the user's other
active sessions.

**Acceptance Criteria:**

- AC-001: An authenticated user can initiate logout.
- AC-002: The server invalidates the current authenticated
  session after successful logout.
- AC-003: Subsequent requests using the logged-out session
  cannot access protected resources.
- AC-004: The user's other active sessions remain unaffected.
- AC-005: The application clears locally stored authentication
  credentials associated with the logged-out session.
- AC-006: Repeated logout requests for an already invalidated
  session do not restore or reactivate that session.

#### FR-AUTH-003 — Logout from All Devices

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow an authenticated user to terminate
all active sessions associated with their account, including
the session used to initiate the operation.

All revoked sessions must become immediately invalid for
subsequent protected requests. The user must authenticate
again to access Voyagrr.

**Acceptance Criteria:**

- AC-001: An authenticated user can initiate logout from
  all devices.
- AC-002: All active sessions belonging to the user
  are invalidated.
- AC-003: The session used to initiate the operation
  is also invalidated.
- AC-004: Subsequent requests using revoked sessions
  cannot access protected resources.
- AC-005: Affected client applications clear their locally
  stored authentication credentials when they become aware
  that their sessions have been revoked.
- AC-006: The user must authenticate again to access
  protected resources.
- AC-007: Sessions belonging to other users remain unaffected.
- AC-008: The operation does not require an additional
  password confirmation when initiated from an authenticated
  session.

#### FR-AUTH-004 — Session Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow authenticated users to view and
manage their active sessions.

Users must be able to view information about their active
sessions, identify their current session, and revoke
individual sessions without affecting other active sessions.

**Acceptance Criteria:**

- AC-001: An authenticated user can view a list of their
  active sessions.
- AC-002: The system displays available device information
  for each session, including device name and platform.
- AC-003: The system displays the session creation date
  and last activity time.
- AC-004: The system clearly identifies the current session.
- AC-005: A user can revoke an individual active session,
  including the current session.
- AC-006: A revoked session becomes immediately invalid
  for subsequent protected requests.
- AC-007: Revoking an individual session does not affect
  the user's other active sessions.
- AC-008: Users cannot view or revoke sessions belonging
  to other users.
- AC-009: Revoked sessions are no longer displayed in
  the active sessions list.
- AC-010: When the current session is revoked, the user
  is logged out and must authenticate again.

#### FR-AUTH-005 — Change Password

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow authenticated users to change their
password by providing their current password and a new
password.

The system must verify the current password before
accepting the password change.

After a successful password change, the system must
invalidate all active sessions associated with the user,
including the current session. The user must authenticate
again using the new password.

**Acceptance Criteria:**

- AC-001: An authenticated user can initiate a password change.
- AC-002: The user must provide their current password.
- AC-003: The system verifies the current password.
- AC-004: The user must provide a new password that meets
  the configured password policy.
- AC-005: The system rejects an incorrect current password.
- AC-006: The system updates the user's password after
  successful verification.
- AC-007: All active sessions belonging to the user are
  invalidated after a successful password change.
- AC-008: The session used to initiate the password change
  is also invalidated.
- AC-009: The user must authenticate again using the new
  password.
- AC-010: The previous password can no longer be used
  for authentication.
- AC-011: Sessions belonging to other users remain unaffected.

#### FR-AUTH-006 — Forgot Password / Password Reset

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users who have forgotten their
password to request a password-reset link using their
registered email address.

The system must send a secure, single-use password-reset
link to the registered email address. The link must expire
30 minutes after it is generated.

Users must be able to set a new password through a valid
reset link. After a successful password reset, all active
sessions associated with the user must be invalidated.

The system must send a confirmation email to the user's
registered email address after a successful password reset.

**Acceptance Criteria:**

- AC-001: A user can request a password reset by providing
  an email address.
- AC-002: The system sends a password-reset link to the
  registered email address.
- AC-003: The system does not reveal whether an email
  address is registered.
- AC-004: A password-reset link expires 30 minutes after
  generation.
- AC-005: The system rejects expired password-reset links.
- AC-006: A password-reset link can only be used once.
- AC-007: Requesting a new reset link invalidates any
  previously generated reset link for that user.
- AC-008: The user must enter and confirm a new password.
- AC-009: The new password must satisfy the configured
  password policy.
- AC-010: The system updates the password after successful
  validation.
- AC-011: All active sessions belonging to the user are
  invalidated after a successful password reset.
- AC-012: The user must authenticate again using the
  new password.
- AC-013: The system rejects invalid, expired or already
  used reset links.
- AC-014: The system sends a confirmation email after
  a successful password reset.
- AC-015: The confirmation email must not contain
  the user's new password.

#### FR-AUTH-007 — Password Policy

**Status:** Approved

**Priority:** Must have

**Description:**

The system must enforce a password policy for user
passwords during registration, password changes and
password resets.

Passwords must contain a minimum of 8 characters and
a maximum of 128 characters.

Every password must contain at least one uppercase
letter, one lowercase letter, one numeric digit and
one special character.

The system must reject commonly used and known-compromised
passwords.

**Acceptance Criteria:**

- AC-001: The system rejects passwords shorter than
  8 characters.
- AC-002: The system rejects passwords longer than
  128 characters.
- AC-003: The system requires at least one uppercase letter.
- AC-004: The system requires at least one lowercase letter.
- AC-005: The system requires at least one numeric digit.
- AC-006: The system requires at least one special character.
- AC-007: The system rejects commonly used passwords.
- AC-008: The system rejects passwords identified as
  known-compromised.
- AC-009: The system does not silently truncate passwords.
- AC-010: The password policy applies consistently during
  registration, password changes and password resets.

#### FR-AUTH-008 — Account Lockout and Login Protection

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect user accounts against repeated
failed authentication attempts.

The system must temporarily prevent login after 5
consecutive failed login attempts.

The temporary lockout must last 15 minutes.

**Acceptance Criteria:**

- AC-001: The system tracks consecutive failed login attempts.
- AC-002: The system temporarily locks an account after
  5 consecutive failed login attempts.
- AC-003: The account remains locked for 15 minutes.
- AC-004: The system rejects login attempts while the
  account is locked.
- AC-005: Successful authentication resets the consecutive
  failed login attempt counter.
- AC-006: The system provides a generic authentication
  error without revealing whether the username or password
  was incorrect.
- AC-007: The account becomes eligible for authentication
  again after the lockout period expires.

#### FR-AUTH-009 — Email Verification

**Status:** Approved

**Priority:** Must have

**Description:**

The system must require users to verify their registered
email address before granting access to the main application.

After registration, the system must send an email containing
a secure verification link to the user's registered email
address.

The verification link must expire 24 hours after generation.

Unverified accounts must remain registered but must not
be allowed to access protected application features.

Users must be able to request a new verification email.
Generating a new verification link must invalidate
any previously generated verification link.

**Acceptance Criteria:**

- AC-001: The system sends an email verification link after
  successful user registration.
- AC-002: The verification link is associated with the
  user's registered email address.
- AC-003: The verification link expires 24 hours after
  generation.
- AC-004: The system rejects expired verification links.
- AC-005: A verification link can only be used once.
- AC-006: The system marks the user's email address as
  verified after successful link validation.
- AC-007: Unverified users cannot access protected
  application features.
- AC-008: Verified users can access application features
  according to their assigned permissions.
- AC-009: Users can request a new verification email.
- AC-010: Generating a new verification link invalidates
  the previously generated link.
- AC-011: A newly generated verification link also expires
  after 24 hours.
- AC-012: The system applies rate limiting to verification
  email resend requests.

#### FR-AUTH-010 — User Registration

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to create a Voyagrr account by providing a valid email address, a unique username and a
password that satisfies the configured password policy.

The system must support configurable registration policies, including open registration, invitation-only registration
and disabled registration.

Newly registered users must verify their email address before accessing protected application features.

The system must provide invitation management capabilities for invitation-only registration. Only system administrators
can generate and revoke registration invitations.

**Acceptance Criteria:**

- AC-001: Users must provide an email address during registration.
- AC-002: Users must provide a username during registration.
- AC-003: Users must provide a password that satisfies the configured password policy.
- AC-004: The system validates the supplied email address.
- AC-005: The system enforces username uniqueness within the installation.
- AC-006: Username matching is case-sensitive.
- AC-007: The system creates the account after successful registration validation.
- AC-008: Newly registered accounts must complete email verification.
- AC-009: The system applies the configured registration policy.
- AC-010: The system does not allow the same email address to be registered with multiple accounts within the same
  installation.
- AC-011: Email address uniqueness checks are case-insensitive.
- AC-012: Users can authenticate using their registered email address regardless of its letter casing.
- AC-013: Disabling registration prevents new users from creating accounts.
- AC-014: Changing the registration mode does not prevent existing users from authenticating.
- AC-015: Existing users retain access according to their account status and assigned permissions, regardless of the
  registration mode.
- AC-016: Only system administrators can generate registration invitations.
- AC-017: Users can create accounts in invitation-only mode only when they provide a valid invitation.
- AC-018: The system rejects registration attempts without a valid invitation when invitation-only registration is
  enabled.
- AC-019: Each registration invitation can be used to create exactly one account.
- AC-020: The system invalidates an invitation after successful account registration.
- AC-021: The system rejects attempts to reuse an invitation that has already been used.
- AC-022: An unused registration invitation expires 7 days after generation.
- AC-023: The system rejects expired registration invitations.
- AC-024: Administrators can generate a new invitation when an existing invitation has expired.
- AC-025: System administrators can manually revoke unused registration invitations.
- AC-026: The system rejects registration attempts using revoked invitations.
- AC-027: Revoking an invitation does not affect accounts created using other invitations.
- AC-028: System administrators can generate registration invitations for specific email addresses.
- AC-029: The system can send an invitation link to the specified email address when email delivery is configured.
- AC-030: System administrators can copy and share generated invitation links manually.
- AC-031: Registration invitations must be associated with their intended email address.
- AC-032: The system must prevent an invitation from being used with a different email address.
- AC-033: When registration mode is Open, new users can register without providing an invitation.
- AC-034: When registration mode is Invitation-only, new users must provide a valid invitation.
- AC-035: When registration mode is Disabled, new users cannot create accounts.
- AC-036: Switching registration from Invitation-only to Open does not automatically invalidate existing unused
  invitations.
- AC-037: Existing unused invitations remain valid until they are used, manually revoked or expired.

#### FR-AUTH-011 — Account Activation and Deactivation

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow system administrators to deactivate and reactivate regular user accounts.

Deactivating an account must immediately invalidate all active sessions belonging to the user and prevent further
authentication and access to protected application features.

Reactivating an account must restore the user's ability to authenticate, subject to email verification and assigned
permissions. Previously revoked sessions must not be restored.

Account deactivation must not delete the user's account or personal media.

**Acceptance Criteria:**

- AC-001: System administrators can deactivate regular user accounts.
- AC-002: System administrators can reactivate inactive accounts.
- AC-003: Deactivated users cannot authenticate.
- AC-004: Deactivated users cannot access protected resources.
- AC-005: Deactivating an account immediately invalidates all active sessions belonging to that user.
- AC-006: Subsequent protected requests using revoked sessions are rejected.
- AC-007: Deactivation does not delete the user's account or personal media.
- AC-008: Reactivating an account does not restore previously revoked sessions.
- AC-009: Reactivated users must authenticate again to access Voyagrr.
- AC-010: Only system administrators can deactivate or reactivate regular user accounts.
- AC-011: Regular users cannot deactivate or reactivate accounts belonging to other users.
- AC-012: System administrators cannot deactivate their own administrator account through ordinary user management.
- AC-013: The system must prevent deactivation of the last active system administrator.
- AC-014: The system sends an email notification to the user's registered email address after an administrator
  successfully deactivates their account.
- AC-015: The notification informs the user that their account has been deactivated.
- AC-016: The notification must not contain sensitive account credentials or authentication information.
- AC-017: The system sends an email notification to the user's registered email address after an administrator
  successfully reactivates their account.
- AC-018: The notification informs the user that their account has been reactivated.
- AC-019: The notification must not contain sensitive account credentials or authentication information.
- AC-020: The system allows administrators to provide an optional reason when deactivating an account.
- AC-021: The system records the deactivation reason in the administrative audit log when provided.
- AC-022: The system allows administrators to provide an optional reason when reactivating an account.
- AC-023: The system records the reactivation reason in the administrative audit log when provided.
- AC-024: The deactivation reason may be included in the notification email.
- AC-025: The system allows administrators to choose whether the deactivation reason is disclosed to the affected user.
- AC-026: When disclosure is enabled, the deactivation notification email includes the provided reason.
- AC-027: When disclosure is disabled, the notification contains no deactivation reason.
- AC-028: The disclosure preference does not affect the reason recorded in the administrative audit log.
- AC-029: Only system administrators can view account activation and deactivation audit history.
- AC-030: Regular users cannot access administrative account audit records.
- AC-031: Each audit record identifies the administrator who performed the action.
- AC-032: Each audit record identifies the affected user account.
- AC-033: Each audit record identifies the action performed and its timestamp.
- AC-034: Each audit record includes the reason when a reason was provided.
- AC-035: Account deactivation remains in effect until a system administrator manually reactivates the account.
- AC-036: The system does not automatically reactivate deactivated accounts based on a scheduled date or time.

### FR-AUTH-012 — Account Deletion

**Description**

Voyagrr shall allow regular users to request deletion of their own accounts and system administrators to initiate
deletion of regular user accounts.

User-initiated deletion requires confirmation through the user's registered email address. Administrator-initiated
deletion requires explicit administrator confirmation and a recorded reason, but does not require confirmation from the
affected user.

Once deletion is confirmed, the account shall immediately lose access to the application and all active sessions shall
be revoked. The account and its associated media shall enter a 30-day retention period before permanent deletion.

During the retention period, only an authorized system administrator may restore the account. Restoration shall recover
the retained account data and media, subject to the rules defined below.

Deleting an account shall also revoke its sharing relationships without affecting media owned by other users.

#### Acceptance Criteria

**Deletion permissions and initiation**

- AC-001: A regular user shall be able to request deletion of their own account.
- AC-002: A regular user shall not be able to request deletion of another user's account.
- AC-003: A system administrator shall be able to initiate deletion of a regular user's account.
- AC-004: A system administrator shall not be able to delete their own administrator account through ordinary account
  management.
- AC-005: The last active system administrator account shall not be eligible for deletion.
- AC-006: User-initiated deletion shall require the user to provide their current password.
- AC-007: User-initiated deletion shall require confirmation through a link sent to the user's registered email address.
- AC-008: The email confirmation link shall expire after 24 hours.
- AC-009: The email confirmation link shall be single-use.
- AC-010: Invalid, expired or previously used confirmation links shall be rejected.
- AC-011: The account shall remain active and accessible until the user successfully confirms the deletion request
  through email.
- AC-012: A user shall be able to cancel a pending deletion request before email confirmation.
- AC-013: Cancelling a pending deletion request shall invalidate its confirmation link and leave the account and its
  media unchanged.

**Administrator-initiated deletion**

- AC-014: Administrator-initiated deletion shall not require confirmation from the affected user.
- AC-015: The administrator shall explicitly confirm the deletion action.
- AC-016: The administrator shall provide a reason when initiating deletion of another user's account.
- AC-017: The system shall record the administrator, affected user, reason and timestamp in the administrative audit
  log.
- AC-018: The administrator who initiated deletion shall be identifiable in the audit history.

**Deletion confirmation and account access**

- AC-019: Successful user email confirmation shall immediately disable the account and initiate its deletion lifecycle.
- AC-020: Administrator confirmation of an account deletion shall immediately disable the affected account and initiate
  its deletion lifecycle.
- AC-021: All active sessions belonging to the account shall be revoked immediately when deletion is confirmed.
- AC-022: Subsequent protected requests using revoked sessions shall be rejected.
- AC-023: The account shall not be able to authenticate or access protected application resources during the deletion
  retention period.
- AC-024: The account and its associated media shall be retained for 30 days after deletion is confirmed.
- AC-025: The system shall track the deletion state and retention period so that restoration and permanent deletion can
  be managed reliably.

**Media retention and permanent deletion**

- AC-026: During the 30-day retention period, the deleted user's media shall not be accessible through normal user
  accounts or active sharing links.
- AC-027: During the retention period, the system shall retain the user's original media files and associated metadata
  for possible restoration.
- AC-028: After the 30-day retention period, the system shall permanently delete the user's retained media from active
  application storage.
- AC-029: Permanent deletion shall include original media files, thumbnails, previews, generated media files and
  associated metadata.
- AC-030: Any generated video renditions, including HLS files, shall also be included in permanent deletion.
- AC-031: Permanent deletion shall not affect media owned by other users.
- AC-032: The system shall consider permanent deletion complete only after the applicable retained media and associated
  application data have been deleted from active storage.

**Account restoration**

- AC-033: A system administrator shall be able to restore a deleted user's account during the 30-day retention period.
- AC-034: Regular users shall not be able to restore their own deleted accounts.
- AC-035: Restoration shall not be available after the retention period has ended and permanent deletion has been
  completed.
- AC-036: Restoration shall recover the retained account and associated media, including their associated metadata.
- AC-037: Restoration shall cancel the pending permanent deletion of the retained account and media.
- AC-038: A restored account shall require the user to authenticate again.
- AC-039: Sessions revoked during deletion shall not be restored.
- AC-040: The administrator shall provide a reason when restoring an account.
- AC-041: The system shall record the restoring administrator, affected user, reason and timestamp in the administrative
  audit log.

**Sharing and ownership behavior**

- AC-042: When a media owner confirms account deletion, all active internal and external shares associated with that
  owner's media shall be revoked immediately.
- AC-043: Shared albums owned by the deleted user shall become inaccessible through normal application access.
- AC-044: External sharing links associated with the deleted user's media shall become inaccessible.
- AC-045: If the account is restored within the retention period, its original sharing relationships shall be restored
  where the recipients and shared resources remain eligible.
- AC-046: Shares that expired or were independently revoked before restoration shall remain revoked.
- AC-047: Deleting a recipient's account shall revoke that recipient's access to media shared by other users.
- AC-048: Deleting a recipient's account shall not delete, modify or otherwise affect the original media owned by
  another user.
- AC-049: Revoking sharing access shall not remotely delete copies of media that recipients have already downloaded.

**Backups and deletion history**

- AC-050: Permanent deletion shall remove the affected account's data from active application storage after the 30-day
  retention period.
- AC-051: Historical backups containing deleted account data shall be retained according to the configured
  backup-retention policy.
- AC-052: The system shall not claim that historical backups have been purged merely because permanent deletion from
  active storage has completed.

**Completion notification**

- AC-053: After permanent deletion of an account and its associated media from active application storage has completed,
  the system shall send a final notification email to the affected user's registered email address.
- AC-054: The final notification email shall confirm that the account and associated media have been permanently deleted
  from active application storage.
- AC-055: The final notification email shall not contain passwords, authentication credentials or other sensitive
  account secrets.
- AC-056: The final notification shall not claim that historical backups have also been purged.

#### Notes and Constraints

- The 30-day retention period begins when deletion is confirmed, not when a user first requests deletion.
- Administrator restoration is available only during the retention period.
- The system administrator's SystemOnly permission model remains in effect during deletion and restoration.
  Administrators may initiate and manage these workflows but shall not gain application-level permission to browse or
  download users' private media.
- Permanent deletion applies to active application storage. Historical backup retention is governed separately by the
  configured backup policy.
- Deletion and restoration must be reliable and auditable. Their detailed processing, retry and recovery behavior will
  be specified in the technical architecture.

### 4.2 Media Management

#### FR-MEDIA-001 — Media Upload

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow authenticated users to upload individual or multiple photos and videos to their personal media
libraries.

Users must be able to monitor upload progress, cancel uploads and retry failed uploads. Interrupted uploads should
resume automatically whenever possible.

Users must be able to upload media to their main library, select an existing album or create a new album during the
upload process.

The system must enforce configured file-size limits and individual user storage quotas. Exact duplicate files within a
user's library must be skipped automatically.

**Acceptance Criteria:**

- AC-001: Authenticated users can upload individual photos and videos.
- AC-002: Users can select and upload multiple media files in one operation.
- AC-003: The system displays upload progress and status for each file.
- AC-004: Users can cancel individual uploads or the entire upload batch.
- AC-005: Failed uploads can be retried without restarting successful uploads.
- AC-006: Interrupted uploads resume automatically whenever possible.
- AC-007: The system accepts supported image and video formats only.
- AC-008: The system enforces the administrator-configured maximum file size.
- AC-009: The system enforces individual user storage quotas and prevents uploads that exceed the available quota.
- AC-010: The system automatically skips exact duplicates found in the uploading user's existing library.
- AC-011: Users can upload to their main library, an existing album or a newly created album.
- AC-012: Users receive clear upload results, including successful, failed, skipped and cancelled files.

#### FR-MEDIA-002 — Media Metadata Extraction

**Status:** Approved

**Priority:** Must have

**Description:**

The system must automatically extract supported metadata from uploaded photos and videos in the background.

Extracted metadata may include capture date and time, camera information, image dimensions, orientation, GPS
coordinates, video duration, resolution and codec information.

The system must preserve original media files and store extracted metadata for use in media browsing, timelines and
media details.

**Acceptance Criteria:**

- AC-001: The system automatically extracts supported metadata after a successful media upload.
- AC-002: The system extracts available photo and video metadata.
- AC-003: The system stores extracted metadata for application use.
- AC-004: The system preserves original uploaded media files without modification.
- AC-005: The system automatically retries failed metadata extraction tasks.
- AC-006: Missing or unsupported metadata is handled gracefully.
- AC-007: Essential metadata is displayed in the media library.
- AC-008: A dedicated media details screen displays all available extracted metadata.
- AC-009: Users cannot manually modify extracted metadata.

#### FR-MEDIA-003 — Thumbnail and Preview Generation

**Status:** Approved

**Priority:** Must have

**Description:**

The system must automatically generate thumbnails and previews for uploaded photos and videos in the background to
support efficient media browsing.

The system must generate multiple thumbnail sizes for different display purposes while preserving the original uploaded
media.

**Acceptance Criteria:**

- AC-001: The system automatically generates thumbnails and previews after successful media uploads.
- AC-002: The system generates multiple thumbnail sizes for different display purposes.
- AC-003: The system generates representative thumbnails for supported videos.
- AC-004: The system uses a generic image or video icon when thumbnail generation fails.
- AC-005: The system automatically retries failed thumbnail-generation tasks.
- AC-006: Successfully generated thumbnails replace fallback icons.
- AC-007: Thumbnail and preview generation does not modify original media files.
- AC-008: Thumbnail and preview generation failures do not make original media inaccessible.

#### FR-MEDIA-004 — Media Library Browsing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow authenticated users to browse their personal media libraries using Timeline View and Grid View.

Timeline View must organize media chronologically based on capture date, while Grid View displays media in a thumbnail
grid.

Users must be able to switch between views, sort media and apply basic filters.

**Acceptance Criteria:**

- AC-001: Users can browse their media using Timeline View and Grid View.
- AC-002: Timeline View organizes media chronologically using capture dates.
- AC-003: Grid View displays media in a thumbnail grid.
- AC-004: The system remembers each user's last selected view.
- AC-005: Users can sort media by capture date, upload date and filename.
- AC-006: Users can sort media in ascending or descending order.
- AC-007: Users can filter media by type (Photos or Videos).
- AC-008: Users can filter media by capture date range.
- AC-009: Media without capture dates remains accessible in the library.

#### FR-MEDIA-005 — Media Details and Viewing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to view individual photos and videos within Voyagrr.

Photos must be accessible through a full-screen viewer with a separate media details panel. Videos must play through an
in-app video player with standard playback controls.

The video player must support automatic and manual playback quality selection, fullscreen playback and playback speed
adjustment.

**Acceptance Criteria:**

- AC-001: Users can open photos in a full-screen viewer.
- AC-002: Users can access a separate details panel displaying available media metadata.
- AC-003: Users can navigate between media items.
- AC-004: Users can play videos directly inside Voyagrr.
- AC-005: The video player provides standard playback controls, including play, pause and seeking.
- AC-006: The video player supports fullscreen playback.
- AC-007: The system uses original video playback when compatible and HLS playback when required.
- AC-008: The video player supports automatic playback quality selection.
- AC-009: Users can manually select an available playback quality.
- AC-010: Users can adjust playback speed, including normal and faster or slower playback.
- AC-011: The MVP does not require subtitle support.

#### FR-MEDIA-006 — Album Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to create and manage albums within their personal media libraries.

Users must be able to organize photos and videos into albums, edit album information and delete albums without affecting
the original media files.

**Acceptance Criteria:**

- AC-001: Users can manually create albums in their personal libraries.
- AC-002: Users can provide an album name, description and cover photo.
- AC-003: Users can edit album names, descriptions and cover photos.
- AC-004: Users can add individual or multiple media items to an album.
- AC-005: Users can remove individual or multiple media items from an album.
- AC-006: Only the album owner can manage the album and its contents.
- AC-007: Users can delete albums they own.
- AC-008: Deleting an album does not delete or move its original media to Trash.
- AC-009: Original media remains accessible in the owner's personal library after album deletion.
- AC-010: Deleting an album makes its associated shared-album access unavailable.

#### FR-MEDIA-007 — Media Deletion and Trash

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to delete photos and videos from their personal media libraries.

Deleted media must be moved to Trash by default, where users can review and restore it before permanent deletion. Users
must also have the option to permanently delete media immediately.

The system must support configurable Trash retention and automatic permanent deletion after the configured retention
period.

**Acceptance Criteria:**

- AC-001: Users can move individual or multiple media items to Trash.
- AC-002: Users can permanently delete individual or multiple media items immediately.
- AC-003: Users can access a dedicated Trash section to browse deleted media.
- AC-004: Users can restore individual or multiple media items from Trash.
- AC-005: Restored media returns to the user's personal library.
- AC-006: Users can permanently delete media from Trash.
- AC-007: System administrators can configure the Trash retention period.
- AC-008: The system automatically permanently deletes media after the configured retention period.
- AC-009: Deletion operations affect only media owned by the user.
- AC-010: Permanent deletion removes the original media and its associated generated files.

### FR-MEDIA-008 — Media Search

**Status:** Approved

**Priority:** Medium

**Description:**

The system must allow users to search for photos and videos using available metadata.

Users must be able to search their personal media libraries and media shared with them, while respecting access
permissions.

**Acceptance Criteria:**

- AC-001: Users can search media by filename.
- AC-002: Users can search media by capture date.
- AC-003: Users can search media by media type (Photos or Videos).
- AC-004: Users can search media by location when GPS metadata is available.
- AC-005: Search results include personal media and media shared with the user.
- AC-006: Users can only see search results for media they are authorized to access.
- AC-007: The system displays a clear message when no matching media is found.

### FR-MEDIA-009 — Media Download

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to download original photos and videos from their personal media libraries.

Users must be able to download individual media files, multiple selected files, and entire albums.

Downloads of shared media must respect the permissions granted by the owner.

**Acceptance Criteria:**

- AC-001: Users can download individual photos and videos.
- AC-002: Users can select and download multiple media files in one operation.
- AC-003: Users can download all accessible media within an album.
- AC-004: Downloaded files retain their original quality and format.
- AC-005: Original filenames are preserved.
- AC-006: Users can download shared media only when download permission is granted.
- AC-007: Users cannot download media they are not authorized to access.
- AC-008: The system provides clear feedback for successful and failed downloads.
- AC-009: Large or bulk downloads are handled without unnecessarily blocking the application.

### FR-MEDIA-010 — Media Sharing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to share individual photos, videos, and albums with other registered users and external
recipients.

Users must be able to configure sharing permissions, including view-only and view-and-download access.

External sharing must support secure share links with optional password protection, configurable expiration, and
revocation.

Album sharing must reflect changes made to the album while the share remains active.

Sharing must not transfer ownership of the original media.

**Acceptance Criteria:**

- AC-001: Users can share individual photos and videos with other registered users.
- AC-002: Users can share albums with other registered users.
- AC-003: Users can create external share links for individual media and albums.
- AC-004: External recipients do not need a Voyagrr account to access shared media.
- AC-005: Users can configure view-only or view-and-download permissions.
- AC-006: Users can set an expiration period for external share links.
- AC-007: Users can optionally protect external share links with a password.
- AC-008: Users can revoke active shares at any time.
- AC-009: Access to shared media is removed when the corresponding share is revoked or expires.
- AC-010: Changes to a shared album are reflected in the shared album view.
- AC-011: Removing media from a shared album removes access to that media through the album share.
- AC-012: Separate individual media shares remain active when the same media is removed from a shared album.
- AC-013: Sharing does not transfer ownership of the original media.
- AC-014: Recipients cannot modify or delete the owner's original media.
- AC-015: Revoking a share does not delete copies that recipients have already downloaded.

### 4.3 Storage Management

#### FR-STORAGE-001 — Storage Usage and Quota Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must track storage usage for each registered user and enforce individual storage quotas.

Users must be able to view their own storage usage and available storage space. System administrators must be able to
view storage usage across all users.

The system must support configurable storage quotas and warn users when their storage usage approaches the configured
limit.

**Acceptance Criteria:**

- AC-001: The system tracks storage usage for each user.
- AC-002: Users can view their total storage usage.
- AC-003: Users can view their available storage space.
- AC-004: System administrators can view storage usage across all users.
- AC-005: System administrators can configure individual user storage quotas.
- AC-006: The system prevents uploads that exceed a user's storage quota.
- AC-007: The system warns users when their storage usage approaches their quota.
- AC-008: Media stored in Trash is excluded from the user's storage quota.
- AC-009: Restored media counts toward the user's storage quota again.
- AC-010: The system prevents restoration when it would exceed the user's storage quota.

#### FR-STORAGE-002 — Storage Provider Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support MinIO and other S3-compatible storage providers for storing photos, videos, and generated media
files.

The storage provider must be configurable at the installation level. A single Voyagrr installation must use one storage
provider at a time.

The system must provide a consistent storage interface so that different S3-compatible providers can be used without
changing application-level media management functionality.

**Acceptance Criteria:**

- AC-001: The system supports MinIO as a storage provider.
- AC-002: The system supports other S3-compatible storage providers.
- AC-003: System administrators can configure the storage provider during installation or deployment.
- AC-004: Storage configuration supports endpoint, bucket, region, and access credentials where applicable.
- AC-005: The system uses only one configured storage provider per installation.
- AC-006: The system validates storage connectivity and configuration.
- AC-007: The system stores original media and generated files using the configured provider.
- AC-008: Storage provider operations are handled through a provider-independent storage interface.
- AC-009: Invalid storage configuration or connectivity failures are reported clearly.
- AC-010: Storage credentials are stored securely and are not exposed to regular users.

#### FR-STORAGE-003 — Storage Health and Connectivity Monitoring

**Status:** Approved

**Priority:** Must have

**Description:**

The system must monitor the connectivity and availability of the configured storage provider.

System administrators must be able to view the current storage health status and identify storage connectivity problems.

The system must notify administrators when storage becomes unavailable and when connectivity is restored.

**Acceptance Criteria:**

- AC-001: The system periodically checks connectivity with the configured storage provider.
- AC-002: The system reports storage health as healthy, degraded, or unavailable.
- AC-003: System administrators can view the current storage health status.
- AC-004: The system detects storage connectivity failures.
- AC-005: The system notifies administrators when storage becomes unavailable.
- AC-006: The system notifies administrators when storage connectivity is restored.
- AC-007: Storage connectivity failures are recorded in system logs.
- AC-008: The system must not lose or corrupt existing media when the storage provider is temporarily unavailable.
- AC-009: The system must recover normal storage operations when connectivity is restored.

### 4.4 Background Processing and Job Management

#### FR-JOB-001 — Background Job Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support background jobs for tasks that do not need to be completed during the user's request.

Background jobs must be managed through a durable PostgreSQL-based job queue to ensure jobs are not lost during
application restarts or temporary failures.

The system must track job status, execution attempts, and completion results.

**Acceptance Criteria:**

- AC-001: The system supports background jobs for media processing and other asynchronous operations.
- AC-002: Background jobs are stored in PostgreSQL.
- AC-003: Jobs support pending, processing, completed, and failed statuses.
- AC-004: The system assigns eligible pending jobs to available workers.
- AC-005: The system tracks job creation time, start time, completion time, and execution attempts.
- AC-006: The system retries jobs when temporary failures occur.
- AC-007: The system recovers jobs left incomplete due to application or worker failures.
- AC-008: The system prevents multiple workers from processing the same job simultaneously under normal operation.
- AC-009: The system records job failures and error details.
- AC-010: System administrators can view background job status and processing history.
- AC-011: Users can view the processing status of jobs related to their own media.
- AC-012: Users can manually retry eligible failed jobs related to their own media.
- AC-013: System administrators can view and manually retry eligible failed jobs across the system.

#### FR-JOB-002 — Job Scheduling and Prioritization

**Status:** Approved

**Priority:** Must have

**Description:**

The system must schedule background jobs and manage their execution based on job priority and availability of processing
resources.

Jobs must be processed according to their assigned priority while ensuring that lower-priority jobs are not indefinitely
delayed.

The system must support different priorities for media processing and other background operations.

**Acceptance Criteria:**

- AC-001: The system supports priority levels for background jobs.
- AC-002: The system assigns appropriate priorities to different job types.
- AC-003: Higher-priority jobs are scheduled before lower-priority jobs when eligible.
- AC-004: The system processes lower-priority jobs without indefinitely delaying them.
- AC-005: The system schedules jobs based on available worker capacity.
- AC-006: Jobs that cannot be processed immediately remain queued until resources become available.
- AC-007: System administrators can view queued jobs and their assigned priorities.

#### FR-JOB-003 — Job Failure Handling and Recovery

**Status:** Approved

**Priority:** Must have

**Description:**

The system must handle background job failures and recover jobs interrupted by unexpected application or worker
failures.

The system must automatically retry eligible failed jobs according to a configured retry policy.

Jobs that exceed the allowed retry attempts must be marked as failed and remain available for manual retry by authorized
users or administrators.

The system must ensure that retrying jobs does not create duplicate or inconsistent media-processing results.

**Acceptance Criteria:**

- AC-001: The system automatically retries jobs that fail due to temporary errors.
- AC-002: The system supports a configurable maximum number of retry attempts.
- AC-003: The system applies a delay between retry attempts.
- AC-004: Jobs that exceed the maximum retry attempts are marked as failed.
- AC-005: The system records failure reasons and retry history.
- AC-006: The system recovers jobs interrupted by unexpected worker or application failures.
- AC-007: Retried jobs must not create duplicate or inconsistent processing results.
- AC-008: Users can manually retry eligible failed jobs related to their own media.
- AC-009: System administrators can manually retry eligible failed jobs across the system.
- AC-010: The system provides clear job failure information to users and administrators.

#### FR-JOB-004 — Job Monitoring and Administration

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide system administrators with tools to monitor background processing and manage the job queue.

Administrators must be able to view queue health, worker availability, job execution status, and processing failures to
identify and resolve operational issues.

**Acceptance Criteria:**

- AC-001: Administrators can view the total number of pending, processing, completed, and failed jobs.
- AC-002: Administrators can view the status and availability of background workers.
- AC-003: Administrators can view job execution history and failure details.
- AC-004: Administrators can filter jobs by status and job type.
- AC-005: Administrators can identify jobs that have been processing longer than expected.
- AC-006: Administrators can view the overall health of the background job queue.
- AC-007: The system records administrative job management actions for auditing.

### 4.5 System Administration

#### FR-ADMIN-001 — User Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow system administrators to manage registered user accounts.

Administrators must be able to view user information, manage account status, and perform administrative actions
according to their assigned permissions.

The system must ensure that administrative user management does not provide access to users' private media libraries.

**Acceptance Criteria:**

- AC-001: Administrators can view a list of registered users.
- AC-002: Administrators can view individual user account details.
- AC-003: Administrators can search and filter users.
- AC-004: Administrators can activate or deactivate user accounts.
- AC-005: Administrators can view account creation date and account status.
- AC-006: Administrators can view storage usage for individual users.
- AC-007: Administrators can initiate account deletion according to the account deletion policy.
- AC-008: Administrative actions are recorded in the audit log.
- AC-009: Administrators cannot browse, view, download, or modify users' private media through the application.

#### FR-ADMIN-002 — System Configuration Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow administrators to manage installation-wide configuration settings.

Administrators must be able to configure registration policies, upload limits, user storage quotas, Trash retention, and
other supported system settings.

Changes to system configuration must be validated and applied without compromising existing user data or system
stability.

**Acceptance Criteria:**

- AC-001: Administrators can configure the user registration mode (Open, Invitation-only, or Disabled).
- AC-002: Administrators can configure maximum upload file size.
- AC-003: Administrators can configure default and individual user storage quotas.
- AC-004: Administrators can configure Trash retention periods.
- AC-005: Administrators can view current system configuration settings.
- AC-006: The system validates configuration values before saving them.
- AC-007: The system applies configuration changes according to the setting's requirements.
- AC-008: Invalid configuration changes are rejected with clear error messages.
- AC-009: Configuration changes are recorded in the audit log.
- AC-010: Sensitive configuration values and credentials are protected from unauthorized access.

### FR-ADMIN-003 — System Monitoring and Health

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide administrators with visibility into the overall health and operational status of the Voyagrr
installation.

Administrators must be able to monitor system resources, application services, database connectivity, storage
availability, and background processing health.

The system must identify service failures and provide sufficient information to help administrators investigate
operational issues.

**Acceptance Criteria:**

- AC-001: Administrators can view the overall system health status.
- AC-002: Administrators can monitor CPU and memory usage.
- AC-003: Administrators can monitor disk usage and available storage capacity.
- AC-004: Administrators can view the health status of application services.
- AC-005: Administrators can monitor database connectivity and availability.
- AC-006: Administrators can view storage provider health status.
- AC-007: Administrators can view background processing and worker health status.
- AC-008: The system identifies unavailable or unhealthy services.
- AC-009: The system records system health events and service failures in logs.
- AC-010: Administrators can view relevant error information to help troubleshoot system issues.

#### FR-ADMIN-004 — Audit Logging

**Status:** Approved

**Priority:** Must have

**Description:**

The system must maintain audit logs of important user and administrative activities.

Audit logs must help administrators track security-related events, account management actions, system configuration
changes, and other important operations.

Audit records must be protected from unauthorized access and modification.

**Acceptance Criteria:**

- AC-001: The system records successful and failed login attempts.
- AC-002: The system records account creation, activation, deactivation, and deletion events.
- AC-003: The system records password changes and password reset events.
- AC-004: The system records administrative actions performed on user accounts.
- AC-005: The system records system configuration changes.
- AC-006: The system records important storage and background job management events.
- AC-007: Administrators can view and search audit logs.
- AC-008: Audit records include the event type, timestamp, and associated user or administrator where applicable.
- AC-009: Audit logs are protected from unauthorized modification or deletion.
- AC-010: Sensitive information, such as passwords, authentication tokens, and storage credentials, is never recorded in
  audit logs.

#### FR-ADMIN-005 — Backup and Restore Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support automatic scheduled backups and manual backups initiated by system administrators.

Backups must protect important application data, including database records and media files, to support recovery after
accidental data loss or system failures.

System administrators must be able to restore the system using available backups.

The system must provide information about backup status, completion, and failures.

**Acceptance Criteria:**

- AC-001: The system supports automatic scheduled backups.
- AC-002: System administrators can initiate manual backups.
- AC-003: Backups include PostgreSQL database data.
- AC-004: Backups include stored original media and generated media files.
- AC-005: System administrators can configure backup schedules.
- AC-006: The system records backup creation time, status, and completion details.
- AC-007: The system reports backup failures with relevant error information.
- AC-008: System administrators can view available backups.
- AC-009: System administrators can restore database and media data from available backups.
- AC-010: The system validates backup availability and integrity before restoration.
- AC-011: The system provides clear feedback during backup and restore operations.
- AC-012: Backup and restore operations are recorded in the audit log.

#### FR-ADMIN-006 — System Notifications

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide in-app notifications to inform users and system administrators about important events and
activities.

Users must receive notifications related to their accounts, media uploads, media processing, and sharing activities.

System administrators must receive notifications about important system events, service failures, storage problems, and
background processing issues.

Notifications must be relevant to the recipient's role and permissions.

**Acceptance Criteria:**

- AC-001: The system provides an in-app notification mechanism.
- AC-002: Users receive notifications about successful and failed media uploads.
- AC-003: Users receive notifications when media processing succeeds or fails.
- AC-004: Users receive notifications about relevant sharing activities.
- AC-005: Users receive notifications about important account-related events.
- AC-006: Administrators receive notifications about important system health issues.
- AC-007: Administrators receive notifications about storage availability and connectivity problems.
- AC-008: Administrators receive notifications about critical background job failures.
- AC-009: Users can view their notifications.
- AC-010: Users can mark notifications as read.
- AC-011: Notifications are visible only to their intended recipients.
- AC-012: The system records notification creation time and read status.

### 4.6 Security and Privacy

#### FR-SEC-001 — Data Access Control

**Status:** Approved

**Priority:** Must have

**Description:**

The system must enforce strict access controls to protect users' private media and personal information.

Users must only be able to access media they own or media explicitly shared with them.

All media and user resources must be protected against unauthorized access, modification, and deletion.

The system must enforce ownership and sharing permissions across all application operations and API endpoints.

**Acceptance Criteria:**

- AC-001: Each user's media library is private by default.
- AC-002: Users can access and manage only media they own.
- AC-003: Users can access shared media only when an active sharing permission exists.
- AC-004: The system enforces sharing permissions when viewing or downloading shared media.
- AC-005: Users cannot modify or delete another user's original media.
- AC-006: Users cannot access another user's private resources by changing resource identifiers or API requests.
- AC-007: The system validates user permissions for every protected operation.
- AC-008: Revoked or expired sharing permissions immediately prevent further access through the application.
- AC-009: System administrators cannot browse, view, download, or modify users' private media through the application.
- AC-010: Unauthorized access attempts are rejected and recorded where appropriate.

#### FR-SEC-002 — Authentication and Session Security

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect user authentication sessions and ensure that only authenticated users can access protected
application resources.

Authentication tokens and session information must be handled securely to prevent unauthorized access and session
misuse.

The system must support secure session management, token expiration, and session revocation.

**Acceptance Criteria:**

- AC-001: Users must authenticate before accessing protected application resources.
- AC-002: The system uses short-lived access tokens for authenticated API requests.
- AC-003: The system supports rotating refresh tokens to renew authenticated sessions.
- AC-004: Expired or invalid authentication tokens are rejected.
- AC-005: Refresh tokens are securely stored and protected against unauthorized access.
- AC-006: The system invalidates refresh tokens when users log out.
- AC-007: Users can revoke individual active sessions.
- AC-008: Users can terminate all active sessions across their devices.
- AC-009: Password changes invalidate existing authenticated sessions.
- AC-010: The system detects and rejects revoked or reused refresh tokens.
- AC-011: Authentication credentials and tokens are never exposed in application logs.

#### FR-SEC-003 — Data Encryption and Protection

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect sensitive user information and media data through appropriate encryption mechanisms.

Data transferred between clients, backend services, workers, and storage providers must be protected against
unauthorized interception.

Sensitive information stored by the system must be protected against unauthorized access.

**Acceptance Criteria:**

- AC-001: All external application communication must use HTTPS.
- AC-002: Internal service communication must use secure channels where required.
- AC-003: Sensitive authentication information must be stored securely.
- AC-004: Storage credentials and other sensitive configuration values must be protected.
- AC-005: The system must support encryption at rest through the configured storage infrastructure.
- AC-006: Database backups must be protected against unauthorized access.
- AC-007: Encryption keys and secrets must not be hardcoded in application source code.
- AC-008: Sensitive information must not be exposed through application logs or error responses.

#### FR-SEC-004 — Security Monitoring and Threat Protection

**Status:** Approved

**Priority:** Must have

**Description:**

The system must monitor security-related activities and protect against common application security threats.

The system must detect suspicious activities, prevent unauthorized access attempts, and record relevant security events
for investigation.

Security protections must apply consistently across application APIs and user-facing operations.

**Acceptance Criteria:**

- AC-001: The system implements rate limiting for authentication endpoints.
- AC-002: The system protects against brute-force login attempts.
- AC-003: The system validates and sanitizes user inputs to prevent common injection attacks.
- AC-004: The system protects against unauthorized cross-origin requests.
- AC-005: The system validates uploaded files against supported file types and configured size limits.
- AC-006: The system detects and records suspicious authentication activities.
- AC-007: The system rejects requests that violate access control or security policies.
- AC-008: Security-related events are recorded in the audit logs.
- AC-009: Sensitive information is excluded from security logs.
- AC-010: Administrators can review relevant security events for investigation.

#### FR-SEC-005 — Privacy and Data Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect user privacy and ensure that personal information is handled securely throughout its lifecycle.

Users must have control over their personal information and be able to request account deletion according to the defined
account deletion policy.

The system must ensure that personal data is not exposed to unauthorized users or used beyond its intended purpose.

**Acceptance Criteria:**

- AC-001: Users can view their own account information.
- AC-002: Users can request deletion of their accounts according to the account deletion policy.
- AC-003: The system removes personal data according to the defined account deletion and retention policies.
- AC-004: The system prevents unauthorized access to personal information.
- AC-005: Users' private media and personal information are not accessible to other users without explicit permission.
- AC-006: The system does not expose personal information through application logs or error responses.
- AC-007: The system applies defined data retention policies to user and system data.
- AC-008: The system records relevant privacy and data management activities in audit logs.

#### FR-SEC-006 — Secure File Upload and Validation

**Status:** Approved

**Priority:** Must have

**Description:**

The system must validate uploaded files to ensure that only supported and acceptable photo and video files are stored
and processed.

The system must protect against malicious files, invalid file formats, and potentially harmful content during upload and
media processing.

File validation must be performed before uploaded files are accepted for permanent storage and processing.

**Acceptance Criteria:**

- AC-001: The system accepts only supported image and video formats.
- AC-002: The system validates file extensions and actual file content.
- AC-003: The system rejects files that do not match their declared file types.
- AC-004: The system enforces configured maximum file-size limits.
- AC-005: The system rejects corrupted or invalid media files.
- AC-006: The system prevents uploaded files from being executed as application code.
- AC-007: The system validates files before sending them for background processing.
- AC-008: Rejected files are not made available in the user's media library.
- AC-009: The system provides clear error messages for rejected uploads.
- AC-010: File validation failures are recorded in system logs.

### 4.7 Client Application Requirements

#### FR-CLIENT-001 — Android Application Support

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide an Android application that allows users to access and manage their personal media libraries.

The Android application must communicate with the Voyagrr backend through secure REST APIs and provide the core media
management features defined for the MVP.

The application must provide a consistent and responsive user experience across supported Android devices.

**Acceptance Criteria:**

- AC-001: Users can securely log in and log out through the Android application.
- AC-002: Users can view and browse their personal media libraries.
- AC-003: Users can upload individual and multiple photos and videos.
- AC-004: Users can monitor upload progress and retry failed uploads.
- AC-005: Users can view photos and play videos.
- AC-006: Users can manage albums and organize their media.
- AC-007: Users can search and filter their media.
- AC-008: Users can download individual and multiple media files.
- AC-009: Users can manage sharing of their media and albums.
- AC-010: Users can view and manage their notifications.
- AC-011: The application communicates with the backend through authenticated and secure APIs.
- AC-012: The application handles network interruptions and provides appropriate error feedback.

#### FR-CLIENT-002 — Responsive User Interface and Navigation

**Status:** Approved

**Priority:** Must have

**Description:**

The Android application must provide a consistent, intuitive, and responsive user interface.

The application must support different Android screen sizes and orientations while maintaining usability and
accessibility.

Users must be able to navigate between the main application features through a clear and consistent navigation
structure.

**Acceptance Criteria:**

- AC-001: The application provides clear navigation between its main features.
- AC-002: Users can access the media library, albums, sharing, notifications, and account settings.
- AC-003: The interface adapts to different supported Android screen sizes.
- AC-004: The application supports portrait and landscape orientations where appropriate.
- AC-005: The application maintains consistent UI components and interaction patterns.
- AC-006: Loading, empty, and error states are displayed clearly.
- AC-007: Interactive elements are appropriately sized and accessible.
- AC-008: The application maintains usability across supported Android devices.

#### FR-CLIENT-003 — Offline and Network Handling

**Status:** Approved

**Priority:** Must have

**Description:**

The Android application must handle network interruptions and temporary connectivity loss without causing data loss or
unexpected application behavior.

The application must provide appropriate feedback when network connectivity is unavailable and resume supported
operations when connectivity is restored.

**Acceptance Criteria:**

- AC-001: The application detects network connectivity changes.
- AC-002: The application informs users when network connectivity is unavailable.
- AC-003: Interrupted uploads can resume when network connectivity is restored.
- AC-004: Failed network operations can be retried by users.
- AC-005: The application displays appropriate loading and error states during network interruptions.
- AC-006: The application prevents duplicate media uploads when retrying interrupted operations.
- AC-007: The application restores normal operation when network connectivity is available again.
- AC-008: The application does not lose locally stored upload progress during temporary network interruptions.

#### FR-CLIENT-004 — Application Notifications

**Status:** Approved

**Priority:** Must have

**Description:**

The Android application must provide an in-app notification interface that allows users to view and manage notifications
generated by the Voyagrr backend.

Users must be able to identify unread notifications and access relevant information about account activities, media
uploads, processing tasks, and sharing activities.

**Acceptance Criteria:**

- AC-001: Users can view their notifications within the Android application.
- AC-002: The application clearly distinguishes read and unread notifications.
- AC-003: Users can mark individual notifications as read.
- AC-004: Users can mark all notifications as read.
- AC-005: The application displays notification details and creation timestamps.
- AC-006: Users can navigate to relevant application screens from supported notifications.
- AC-007: The application updates notification status when users read notifications.
- AC-008: Users can only access their own notifications.
- AC-009: The application displays an appropriate message when no notifications are available.

#### FR-CLIENT-005 — Application Settings and Preferences

**Status:** Approved

**Priority:** Must have

**Description:**

The Android application must provide a settings interface that allows users to manage their application preferences and
account-related options.

Users must be able to customize supported application preferences without affecting other users or installation-wide
system settings.

**Acceptance Criteria:**

- AC-001: Users can access application settings.
- AC-002: Users can manage their account-related preferences.
- AC-003: Users can configure supported media browsing preferences.
- AC-004: Users can manage notification preferences.
- AC-005: Users can view application information, including the current version.
- AC-006: Users can access privacy and security-related settings.
- AC-007: User preferences are saved and retained across application restarts.
- AC-008: Users can only modify their own application preferences.

### 4.8 API and Integration Requirements

#### FR-API-001 — REST API Support

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide RESTful APIs to allow client applications to communicate with the Voyagrr backend.

The APIs must support the core application functionalities, including authentication, media management, uploads,
downloads, albums, sharing, and user account management.

The API architecture must support future client applications without requiring changes to the core backend
functionality.

**Acceptance Criteria:**

- AC-001: The system exposes RESTful APIs for client application communication.
- AC-002: APIs support authentication and authorization.
- AC-003: APIs support media upload, download, viewing, and management operations.
- AC-004: APIs support album creation and management.
- AC-005: APIs support media sharing and permission management.
- AC-006: APIs support user account and profile management.
- AC-007: APIs enforce ownership and access permissions for protected resources.
- AC-008: APIs use consistent request and response formats.
- AC-009: APIs provide appropriate HTTP status codes and error responses.
- AC-010: APIs support versioning to maintain compatibility with future client applications.
- AC-011: API documentation is maintained using the OpenAPI specification.
- AC-012: APIs are accessible only through secure communication channels.

#### FR-API-002 — API Authentication and Authorization

**Status:** Approved

**Priority:** Must have

**Description:**

The system must authenticate API requests and enforce authorization before allowing access to protected resources.

The system must use the established JWT-based authentication mechanism with short-lived access tokens and rotating
refresh tokens.

API authorization must enforce user ownership, sharing permissions, and administrative access restrictions.

**Acceptance Criteria:**

- AC-001: Protected API endpoints require valid authentication.
- AC-002: The system validates access tokens for authenticated API requests.
- AC-003: Expired, invalid, or revoked tokens are rejected.
- AC-004: The system enforces role-based permissions for administrative operations.
- AC-005: Users can access only resources they own or have permission to access.
- AC-006: Shared media access is governed by the applicable sharing permissions.
- AC-007: Administrative API access follows the defined SystemOnly administrator permissions.
- AC-008: Unauthorized API requests return appropriate HTTP status codes.
- AC-009: The system prevents users from accessing or modifying resources by manipulating API resource identifiers.
- AC-010: Authentication and authorization failures are recorded without exposing sensitive information.

#### FR-API-003 — API Rate Limiting and Request Validation

**Status:** Approved

**Priority:** Must have

**Description:**

The system must implement API rate limiting to protect backend services from excessive requests and potential abuse.

The system must validate incoming API requests to ensure that submitted data follows the expected formats, constraints,
and business rules.

Rate limits and validation rules must be applied consistently across relevant API endpoints.

**Acceptance Criteria:**

- AC-001: The system enforces rate limits on API endpoints.
- AC-002: Authentication endpoints have appropriate rate limits to prevent brute-force attempts.
- AC-003: The system limits excessive requests from individual users or clients.
- AC-004: Requests exceeding configured rate limits are rejected with appropriate HTTP status codes.
- AC-005: Rate-limited responses provide information about when requests can be retried where applicable.
- AC-006: The system validates request parameters, headers, and request bodies.
- AC-007: Invalid or malformed requests are rejected with clear error responses.
- AC-008: The system validates uploaded file metadata and request constraints.
- AC-009: API validation failures are recorded without exposing sensitive information.
- AC-010: Rate limiting and request validation do not bypass authentication or authorization checks.

#### FR-API-004 — API Documentation and Developer Support

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide comprehensive and up-to-date API documentation for developers and contributors.

API documentation must describe available endpoints, request and response formats, authentication requirements, error
responses, and supported operations.

The documentation must help developers integrate future client applications with the Voyagrr backend.

**Acceptance Criteria:**

- AC-001: The system maintains API documentation using the OpenAPI specification.
- AC-002: API documentation includes available endpoints and supported HTTP methods.
- AC-003: API documentation describes request parameters, headers, and request bodies.
- AC-004: API documentation describes response formats and HTTP status codes.
- AC-005: API documentation includes authentication and authorization requirements.
- AC-006: API documentation describes common API errors and their responses.
- AC-007: Developers can access interactive API documentation for testing supported endpoints.
- AC-008: API documentation is updated when API contracts change.
- AC-009: API documentation supports developers building future Voyagrr client applications.

### 4.9 Media Processing Requirements

#### FR-PROC-001 — Image Processing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must process uploaded images in the background to prepare them for efficient viewing and browsing.

Image processing must preserve the original uploaded files while generating optimized versions and previews for
application use.

The system must handle supported image formats and recover from processing failures without affecting original media.

**Acceptance Criteria:**

- AC-001: The system processes supported uploaded image formats in the background.
- AC-002: The system preserves original uploaded image files without modification.
- AC-003: The system generates optimized image versions for application viewing.
- AC-004: The system generates image previews and thumbnails in supported sizes.
- AC-005: The system maintains the image's aspect ratio during processing.
- AC-006: The system handles image orientation correctly using available metadata.
- AC-007: The system stores generated image files using the configured storage provider.
- AC-008: The system automatically retries eligible image-processing failures.
- AC-009: Image-processing failures do not make the original uploaded image inaccessible.
- AC-010: The system records image-processing status and failure details.

#### FR-PROC-002 — Video Processing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must process uploaded videos in the background to prepare them for efficient playback and browsing.

The system must preserve original video files and generate optimized playback versions when required.

Video processing must support the hybrid playback approach, using original videos when compatible and HLS streaming when
transcoding is required.

**Acceptance Criteria:**

- AC-001: The system processes supported uploaded video formats in the background.
- AC-002: The system preserves original uploaded video files without modification.
- AC-003: The system generates representative thumbnails and previews for supported videos.
- AC-004: The system checks whether an original video is compatible with supported playback requirements.
- AC-005: The system generates HLS playback versions when required.
- AC-006: The system supports appropriate video resolutions and quality levels for generated playback versions.
- AC-007: The system stores generated video files using the configured storage provider.
- AC-008: The system automatically retries eligible video-processing failures.
- AC-009: Video-processing failures do not make the original uploaded video inaccessible.
- AC-010: The system records video-processing status and failure details.
- AC-011: Users can play the original video when compatible or use the generated HLS version when required.

#### FR-PROC-003 — Metadata Processing and Extraction

**Status:** Approved

**Priority:** Must have

**Description:**

The system must process media metadata extraction tasks through the background processing system.

Metadata extraction must support the identification and storage of available image and video metadata for use by other
application features.

The system must handle unsupported or incomplete metadata without interrupting other media-processing operations.

**Acceptance Criteria:**

- AC-001: Metadata extraction runs as a background processing task.
- AC-002: The system extracts available metadata from supported image and video formats.
- AC-003: Extracted metadata is stored in the database for application use.
- AC-004: The system handles missing or unsupported metadata without failing the entire processing task unnecessarily.
- AC-005: Metadata extraction failures are recorded with relevant error details.
- AC-006: Eligible failed metadata extraction jobs can be retried.
- AC-007: Metadata extraction does not modify original media files.
- AC-008: Successful metadata extraction updates the media processing status.

#### FR-PROC-004 — Duplicate Media Detection

**Status:** Approved

**Priority:** Must have

**Description:**

The system must detect exact duplicate photos and videos uploaded by users.

Duplicate detection must use SHA-256 checksums to identify files with identical content, regardless of their filenames.

Duplicate detection must be performed within the uploading user's personal media library without affecting other users'
media.

**Acceptance Criteria:**

- AC-001: The system calculates a SHA-256 checksum for uploaded media files.
- AC-002: The system compares uploaded file checksums against existing media owned by the same user.
- AC-003: The system identifies files with matching checksums as exact duplicates.
- AC-004: Duplicate files are skipped instead of being stored again.
- AC-005: The system informs users when an uploaded file is identified as a duplicate.
- AC-006: Duplicate detection does not compare media across different users' libraries.
- AC-007: Files with different content are treated as separate media, even when their filenames are identical.
- AC-008: The system records duplicate detection results as part of the upload operation.

#### FR-PROC-005 — Media Processing Status and Tracking

**Status:** Approved

**Priority:** Must have

**Description:**

The system must track the processing status of uploaded photos and videos throughout their processing lifecycle.

Users must be able to view the processing status of their own media, while system administrators must be able to monitor
processing activities across the installation.

The system must provide clear information about processing progress, completion, and failures.

**Acceptance Criteria:**

- AC-001: The system tracks processing status for uploaded media.
- AC-002: The system supports pending, processing, completed, and failed processing statuses.
- AC-003: Users can view the processing status of their own media.
- AC-004: System administrators can view media-processing status across all users.
- AC-005: The system updates processing status as tasks progress.
- AC-006: The system records processing start time and completion time.
- AC-007: Users can identify media-processing failures related to their own media.
- AC-008: The system displays relevant failure information without exposing sensitive technical details.
- AC-009: Successful processing updates the media status accordingly.
- AC-010: Processing failures do not prevent users from accessing their original uploaded media.

### 4.10 Sharing and Collaboration Requirements

#### FR-SHARE-001 — Shared Media Access

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide users with a dedicated interface to access photos, videos, and albums shared with them by other
users.

Users must be able to view shared media according to the permissions granted by the owner.

The system must keep shared media separate from users' personally owned media libraries.

**Acceptance Criteria:**

- AC-001: Users can view a list of media shared with them.
- AC-002: Users can view albums shared with them.
- AC-003: Users can access shared media according to the granted permissions.
- AC-004: Users can identify the owner of shared media.
- AC-005: Users can view sharing details, including available permissions and expiration information.
- AC-006: Users cannot modify or delete the owner's original media.
- AC-007: Expired or revoked shares are no longer accessible.
- AC-008: Shared media does not become part of the recipient's personal media library.
- AC-009: Users can access shared media only while the corresponding sharing permission remains valid.

#### FR-SHARE-002 — Share Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to manage shares created for their photos, videos, and albums.

Users must be able to view active shares, review sharing permissions, update supported sharing settings, and revoke
access when required.

The system must ensure that only the owner of the shared media can manage its sharing permissions.

**Acceptance Criteria:**

- AC-001: Users can view a list of shares they have created.
- AC-002: Users can view details of individual shares.
- AC-003: Users can identify the recipients of their internal shares.
- AC-004: Users can view the permissions and expiration details of each share.
- AC-005: Users can update supported sharing permissions.
- AC-006: Users can update the expiration period of active shares.
- AC-007: Users can revoke active shares.
- AC-008: Users can identify expired and revoked shares.
- AC-009: Only the owner of the shared media can manage its sharing settings.
- AC-010: Changes to sharing permissions take effect for subsequent access attempts.

#### FR-SHARE-003 — External Sharing

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow users to share photos, videos, and albums with external recipients through secure share links.

External recipients must be able to access shared media without creating a Voyagrr account.

The system must enforce configured sharing permissions, password protection, expiration periods, and revocation rules
for external share links.

**Acceptance Criteria:**

- AC-001: Users can create external share links for individual photos and videos.
- AC-002: Users can create external share links for albums.
- AC-003: External recipients can access shared media without a Voyagrr account.
- AC-004: External share links support view-only and view-and-download permissions.
- AC-005: Users can optionally protect external share links with a password.
- AC-006: Users can configure expiration periods for external share links.
- AC-007: Users can revoke external share links at any time.
- AC-008: Expired or revoked links no longer provide access to shared media.
- AC-009: External recipients can only access media included in the corresponding share.
- AC-010: External sharing does not transfer ownership of the original media.

#### FR-SHARE-004 — Album Collaboration

**Status:** Approved

**Priority:** Must have

**Description:**

The system must allow album owners to enable media contributions from other users to shared albums.

Authorized contributors must be able to add their own photos and videos to collaborative albums while the album owner
retains control over the album.

The system must maintain ownership of contributed media and ensure that contributors can manage only their own
contributions.

**Acceptance Criteria:**

- AC-001: Album owners can enable or disable media contributions for shared albums.
- AC-002: Authorized contributors can upload photos and videos to collaborative albums.
- AC-003: Contributors can view media added to collaborative albums.
- AC-004: Contributors can remove their own media contributions.
- AC-005: Album owners can view who contributed each media item and when it was added.
- AC-006: Album owners retain control over collaborative album settings and content.
- AC-007: Contributors cannot modify or delete media belonging to other users.
- AC-008: Revoking a contributor's access removes their contributed media from the shared album.
- AC-009: Removing contributed media from an album does not delete the original media from its owner's library.

#### FR-SHARE-005 — Sharing Activity and Notifications

**Status:** Approved

**Priority:** Must have

**Description:**

The system must notify users about important activities related to media and album sharing.

Users must receive relevant notifications when they are invited to access shared media, when sharing permissions change,
and when access is revoked or expires.

Album owners must be able to stay informed about contributions made to their collaborative albums.

**Acceptance Criteria:**

- AC-001: Users receive notifications when another user shares media or albums with them.
- AC-002: Users receive notifications when their sharing permissions are changed.
- AC-003: Users receive notifications when access to shared media is revoked.
- AC-004: Users receive notifications when a share expires.
- AC-005: Album owners receive notifications when contributors add media to collaborative albums.
- AC-006: Album owners receive notifications when contributors remove their media from collaborative albums.
- AC-007: Notifications are delivered through the in-app notification system.
- AC-008: Users can access relevant shared media or album details from supported notifications.
- AC-009: Users can only view sharing notifications intended for them.

### 4.11 Deployment and Installation Requirements

#### FR-DEPLOY-001 — Docker Compose Deployment

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support deployment using Docker Compose to simplify installation and management on user-owned VPS or
server infrastructure.

The deployment must include the required Voyagrr services, including the backend, media processing worker, database, and
configured storage provider.

The deployment process must be documented and provide a straightforward setup experience for self-hosted installations.

**Acceptance Criteria:**

- AC-001: The system provides Docker Compose configuration for deployment.
- AC-002: The deployment includes all required application services.
- AC-003: The deployment supports PostgreSQL for database and background job management.
- AC-004: The deployment supports MinIO or another configured S3-compatible storage provider.
- AC-005: Services can be started, stopped, and restarted using Docker Compose.
- AC-006: Persistent data is stored using appropriate Docker volumes or configured storage locations.
- AC-007: The deployment supports environment-based configuration.
- AC-008: Sensitive credentials are not hardcoded in deployment configuration.
- AC-009: The system provides health checks for required services.
- AC-010: Installation and deployment instructions are documented.
- AC-011: The deployment can be performed on supported Linux-based VPS or server environments.

#### FR-DEPLOY-002 — Installation and Initial Setup

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide a straightforward initial setup process for newly deployed Voyagrr installations.

The setup process must allow administrators to configure essential system settings, establish the initial administrator
account, and verify required service connectivity.

The system must validate the initial configuration before allowing the installation to be used.

**Acceptance Criteria:**

- AC-001: The system provides instructions for completing the initial setup.
- AC-002: The initial setup allows creation of the first system administrator account.
- AC-003: The system supports configuration of essential application settings.
- AC-004: The system validates database connectivity during initial setup.
- AC-005: The system validates connectivity with the configured storage provider.
- AC-006: The system validates required service configurations.
- AC-007: The system reports configuration errors with clear messages.
- AC-008: The system confirms successful completion of the initial setup.
- AC-009: The initial setup process cannot be used to create additional administrator accounts without proper
  authorization.
- AC-010: Sensitive configuration values are handled securely during setup.

#### FR-DEPLOY-003 — Application Updates and Version Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support application updates to allow self-hosted users to install new Voyagrr releases.

The update process must preserve existing user data, media files, and system configuration.

The system must support database migrations and provide clear information about application versions and update status.

**Acceptance Criteria:**

- AC-001: The system provides a documented application update process.
- AC-002: Users can update Voyagrr using the provided Docker Compose deployment configuration.
- AC-003: Application updates preserve existing database records and user media.
- AC-004: Application updates preserve existing system configuration.
- AC-005: The system applies required database migrations during updates.
- AC-006: The system reports update failures with relevant error information.
- AC-007: Users can view the currently installed application version.
- AC-008: The system provides information about available application releases.
- AC-009: The update process includes backup recommendations before applying updates.
- AC-010: Application version information is consistent across deployed services.

#### FR-DEPLOY-004 — Configuration and Environment Management

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support environment-based configuration to allow users to customize their Voyagrr installation.

Deployment settings must be configurable without modifying application source code.

Sensitive configuration values, including passwords, access credentials, and secret keys, must be handled securely.

The system must validate configuration values and report errors when required settings are missing or invalid.

**Acceptance Criteria:**

- AC-001: The system supports environment-based configuration.
- AC-002: Users can configure deployment settings through environment variables or configuration files.
- AC-003: The system supports separate configuration for application services.
- AC-004: Sensitive credentials and secret keys are not hardcoded in application source code.
- AC-005: Sensitive configuration values are protected from unauthorized access.
- AC-006: The system validates required configuration values during startup.
- AC-007: The system reports missing or invalid configuration values with clear error messages.
- AC-008: Configuration changes can be applied through the documented deployment process.
- AC-009: Configuration changes do not unintentionally overwrite existing user data or media.

#### FR-DEPLOY-005 — Deployment Troubleshooting and Logs

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide logging and troubleshooting capabilities to help administrators identify and resolve deployment
and operational issues.

Administrators must be able to access relevant service logs, identify application errors, and diagnose problems
affecting Voyagrr services.

Logs must provide useful diagnostic information without exposing sensitive user information or credentials.

**Acceptance Criteria:**

- AC-001: The system generates logs for application services.
- AC-002: Administrators can access logs for individual services.
- AC-003: Logs include timestamps, severity levels, and relevant error details.
- AC-004: The system records application startup and shutdown events.
- AC-005: The system records service failures and connectivity problems.
- AC-006: Administrators can identify common deployment and configuration errors through logs.
- AC-007: Sensitive information, including passwords, authentication tokens, and storage credentials, is excluded from
  logs.
- AC-008: The system supports log rotation to prevent unlimited log growth.
- AC-009: Deployment documentation includes common troubleshooting steps.
- AC-010: The system provides sufficient diagnostic information to help administrators resolve service issues.

### 4.12 Data Management Requirements

#### FR-DATA-001 — Database Management and Integrity

**Status:** Approved

**Priority:** Must have

**Description:**

The system must use PostgreSQL as the primary database for storing and managing application data.

The database must maintain data consistency and integrity across users, media records, albums, sharing permissions, and
background jobs.

The system must ensure that database operations are reliable and protect stored information against inconsistent or
invalid data.

**Acceptance Criteria:**

- AC-001: The system uses PostgreSQL as its primary application database.
- AC-002: The system maintains relationships between users, media, albums, and sharing records.
- AC-003: The system enforces database constraints to maintain data integrity.
- AC-004: The system uses database transactions for operations requiring atomicity.
- AC-005: The system prevents invalid or inconsistent data from being stored.
- AC-006: The system supports database schema migrations using Liquibase.
- AC-007: Database migrations are version-controlled and applied in a controlled manner.
- AC-008: The system handles database connectivity failures gracefully.
- AC-009: The system protects database access through appropriate authentication and permissions.
- AC-010: Database operations do not expose unauthorized users' private information.

#### FR-DATA-002 — Data Migration and Schema Versioning

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support controlled database schema migrations to manage changes to the database structure across
application releases.

Database migrations must be version-controlled and executed in a predictable manner to maintain compatibility with the
application.

The migration process must preserve existing user data and maintain database integrity.

**Acceptance Criteria:**

- AC-001: The system uses Liquibase to manage database schema migrations.
- AC-002: Database changes are maintained through version-controlled migration files.
- AC-003: Each migration has a unique identifier and execution order.
- AC-004: The system tracks previously executed database migrations.
- AC-005: The system applies pending migrations during application updates.
- AC-006: The system prevents the same migration from being executed multiple times.
- AC-007: Migration failures are recorded with relevant error details.
- AC-008: The system prevents application startup when required migrations fail.
- AC-009: Database migrations preserve existing user data unless an explicitly documented data migration requires
  changes.
- AC-010: The system validates database schema compatibility with the application version.

#### FR-DATA-003 — Data Retention and Cleanup

**Status:** Approved

**Priority:** Must have

**Description:**

The system must manage data retention and cleanup to prevent unnecessary storage consumption and maintain system
efficiency.

The system must automatically remove temporary files, expired data, and permanently deleted media according to
configured retention policies.

The cleanup process must ensure that active user media and required application data are not accidentally deleted.

**Acceptance Criteria:**

- AC-001: The system automatically cleans up temporary files generated during uploads, downloads, and media processing.
- AC-002: Temporary files are removed after their associated operations are completed or abandoned.
- AC-003: The system permanently deletes media from storage after the configured Trash retention period.
- AC-004: Permanently deleted media includes associated thumbnails, previews, and generated processing files.
- AC-005: Expired external sharing data is cleaned up according to the defined retention policy.
- AC-006: The system prevents cleanup operations from deleting active or accessible user media.
- AC-007: System administrators can configure supported data retention periods.
- AC-008: Cleanup operations run automatically through background jobs.
- AC-009: The system records cleanup execution status and failures.
- AC-010: Failed cleanup operations can be retried.

## 5. Non-Functional Requirements

This section defines the quality attributes, performance expectations, security standards, reliability requirements, and
operational constraints of Voyagrr.

These requirements describe how the system must operate to provide a secure, reliable, efficient, and maintainable
experience.

### 5.1 Performance and Responsiveness

#### NFR-001 — API Response Time

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide responsive API interactions to ensure a smooth user experience across supported client
applications.

Standard API requests should complete within defined response-time targets under normal operating conditions.

Long-running operations, including media uploads, downloads, and background processing, must not unnecessarily block API
responses.

**Acceptance Criteria:**

- AC-001: Standard API requests should complete within 2 seconds under normal operating conditions.
- AC-002: At least 95% of standard API requests should complete within the defined response-time target.
- AC-003: API requests involving long-running operations must use asynchronous processing where appropriate.
- AC-004: The system must provide appropriate loading or progress information for long-running operations.
- AC-005: Performance must be evaluated under the expected MVP workload.

#### NFR-002 — Media Loading and Browsing Performance

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide fast and responsive media browsing to ensure a smooth user experience.

Media thumbnails, previews, and library views must load efficiently, even when users have large media collections.

The system must optimize media delivery without unnecessarily affecting original media quality.

**Acceptance Criteria:**

- AC-001: Media library views should load within 3 seconds under normal operating conditions.
- AC-002: Media thumbnails should load progressively without blocking the entire library view.
- AC-003: Thumbnails should load within 1 second under normal operating conditions.
- AC-004: The system must support pagination or lazy loading for large media collections.
- AC-005: Photos should display efficiently using generated previews where appropriate.
- AC-006: Video playback should begin within 5 seconds under normal network conditions.
- AC-007: The system must avoid loading unnecessary full-resolution media during library browsing.
- AC-008: Media loading performance must be evaluated using representative library sizes.

#### NFR-003 — Upload and Download Performance

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide efficient and reliable media upload and download operations.

Large media files and bulk transfers must be handled without unnecessarily blocking application operations.

The system must support resumable uploads and efficient media downloads while maintaining file integrity.

**Acceptance Criteria:**

- AC-001: The system supports multipart uploads for large media files.
- AC-002: Interrupted uploads can resume without restarting from the beginning where supported.
- AC-003: The system provides upload progress information to users.
- AC-004: Upload operations must not unnecessarily block other application requests.
- AC-005: The system supports individual and bulk media downloads.
- AC-006: Large and bulk downloads are handled efficiently without unnecessarily blocking the application.
- AC-007: The system preserves original file quality and integrity during uploads and downloads.
- AC-008: Upload and download performance is evaluated under representative network conditions.
- AC-009: The system handles temporary network interruptions without causing data corruption.

### 5.2 Security and Privacy

#### NFR-004 — Data Security

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect application data, user information, and media files against unauthorized access, modification,
and disclosure.

Security controls must be consistently enforced across backend services, client applications, and storage operations.

**Acceptance Criteria:**

- AC-001: All external communication must use HTTPS.
- AC-002: Sensitive information must be protected during transmission and storage.
- AC-003: Authentication and authorization must be enforced for protected resources.
- AC-004: Passwords must be stored using secure password hashing.
- AC-005: Secrets and credentials must not be hardcoded in application source code.
- AC-006: The system must prevent unauthorized access to users' private media.
- AC-007: Security-sensitive operations must be recorded in audit logs.
- AC-008: The system must follow established secure development practices.

#### NFR-005 — Data Privacy

**Status:** Approved

**Priority:** Must have

**Description:**

The system must protect user privacy and maintain isolation between individual users' personal media libraries.

Personal information and media must only be accessible to authorized users according to ownership and sharing
permissions.

**Acceptance Criteria:**

- AC-001: Each user's personal media library remains private by default.
- AC-002: Users cannot access another user's private media without explicit sharing permission.
- AC-003: System administrators cannot browse users' private media through the application.
- AC-004: Sensitive personal information must not be unnecessarily exposed through logs or error responses.
- AC-005: User data must be handled according to defined retention and deletion policies.
- AC-006: Sharing permissions must be enforced consistently across application operations.

#### NFR-006 — Vulnerability and Security Maintenance

**Status:** Approved

**Priority:** Must have

**Description:**

The system must be maintained against known security vulnerabilities affecting its application code, dependencies, and
deployment components.

Security issues must be identified, assessed, and addressed through the project's maintenance process.

**Acceptance Criteria:**

- AC-001: Project dependencies are regularly checked for known vulnerabilities.
- AC-002: Critical security vulnerabilities are prioritized for remediation.
- AC-003: Security updates are incorporated into supported releases.
- AC-004: The project maintains a process for reporting security vulnerabilities.
- AC-005: Security-sensitive changes undergo appropriate review and testing.

### 5.3 Reliability and Availability

#### NFR-007 — System Reliability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must operate reliably and handle unexpected failures without causing unnecessary data loss or corruption.

Application services must recover gracefully from temporary failures and interruptions.

**Acceptance Criteria:**

- AC-001: Temporary service failures must not cause permanent loss of successfully stored media.
- AC-002: Database transactions must maintain data consistency.
- AC-003: Background jobs must recover from unexpected worker or application interruptions.
- AC-004: Failed operations must provide appropriate error information.
- AC-005: The system must recover normal operations after temporary service failures.
- AC-006: The system must preserve original media when media processing fails.

#### NFR-008 — Service Availability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide consistent service availability for self-hosted installations.

Required application services must remain operational during normal usage and recover from unexpected service
interruptions.

**Acceptance Criteria:**

- AC-001: Required services must expose health status information.
- AC-002: Failed services must be restartable through the documented deployment process.
- AC-003: The system must detect unavailable database and storage services.
- AC-004: Temporary service interruptions must not cause unnecessary data loss.
- AC-005: The system must provide sufficient operational information to diagnose availability problems.

#### NFR-009 — Fault Tolerance and Recovery

**Status:** Approved

**Priority:** Must have

**Description:**

The system must tolerate temporary failures in application services, background workers, database connectivity, and
storage connectivity.

Failures in one component should not unnecessarily cause the entire platform to become unavailable.

**Acceptance Criteria:**

- AC-001: Background worker failures must not cause permanent loss of queued jobs.
- AC-002: Temporary storage connectivity failures must not corrupt existing media.
- AC-003: Interrupted background jobs must be recoverable.
- AC-004: Failed operations must be retryable where appropriate.
- AC-005: The system must handle unavailable dependencies gracefully.
- AC-006: Recovery operations must preserve data consistency.

### 5.4 Scalability

#### NFR-010 — User and Media Scalability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support growth in the number of registered users and the size of individual media libraries without
requiring major architectural changes.

The platform must support the expected MVP workload while allowing future capacity expansion.

**Acceptance Criteria:**

- AC-001: The system supports the initial target of 1–5 users per installation.
- AC-002: The system supports media libraries containing up to 100,000 files.
- AC-003: Media browsing must remain responsive as library sizes increase.
- AC-004: The system supports configurable storage capacity according to available infrastructure.
- AC-005: The architecture must allow processing capacity to increase independently of the backend application where
  appropriate.

#### NFR-011 — Background Processing Scalability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support increasing background processing workloads without requiring changes to the core application
architecture.

Processing capacity must be adjustable according to available server resources and workload requirements.

**Acceptance Criteria:**

- AC-001: The system supports multiple background processing workers.
- AC-002: Processing workloads can be distributed across available workers.
- AC-003: Worker capacity can be adjusted according to available resources.
- AC-004: Increasing worker capacity must not cause duplicate processing of the same active job.
- AC-005: Background processing must not unnecessarily affect normal API responsiveness.

### 5.5 Usability and Accessibility

#### NFR-012 — User Experience

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide an intuitive and consistent user experience across supported client applications.

Users must be able to access common features without unnecessary complexity.

**Acceptance Criteria:**

- AC-001: The Android application provides consistent navigation and interaction patterns.
- AC-002: Common operations must be accessible through clear application interfaces.
- AC-003: The system provides understandable success, loading, empty, and error states.
- AC-004: Important user actions must provide appropriate feedback.
- AC-005: The application must maintain usability across supported screen sizes.
- AC-006: Application interfaces must avoid unnecessary complexity.

#### NFR-013 — Accessibility

**Status:** Approved

**Priority:** Should have

**Description:**

The Android application should follow accessibility best practices to ensure that users with different abilities can
access its core features.

**Acceptance Criteria:**

- AC-001: Interactive elements provide meaningful accessibility labels.
- AC-002: Text remains readable at supported system font sizes.
- AC-003: Important information must not rely exclusively on color.
- AC-004: Interactive elements provide sufficient touch target sizes.
- AC-005: Core navigation and actions support Android accessibility services.

### 5.6 Maintainability and Extensibility

#### NFR-014 — Code Maintainability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must maintain a modular and understandable codebase to support ongoing development, maintenance, and
contributions from the open-source community.

Application modules must have clearly defined responsibilities and interfaces.

**Acceptance Criteria:**

- AC-001: Backend functionality is organized into clearly defined modules.
- AC-002: Modules maintain clear boundaries and responsibilities.
- AC-003: Common functionality must avoid unnecessary duplication.
- AC-004: The codebase follows consistent coding conventions.
- AC-005: Important architectural decisions are documented.
- AC-006: Automated tests cover critical application functionality.
- AC-007: Changes must not unnecessarily break existing module contracts.

#### NFR-015 — Extensibility

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support future feature development and integration without requiring major changes to existing core
functionality.

The architecture must allow future client applications, storage providers, and application modules to be introduced
independently where appropriate.

**Acceptance Criteria:**

- AC-001: The backend architecture supports adding new functional modules.
- AC-002: The storage abstraction supports additional S3-compatible providers.
- AC-003: The API architecture supports future client applications.
- AC-004: New features should not require unnecessary changes to unrelated modules.
- AC-005: The system supports future expansion without requiring an immediate microservices architecture.

#### NFR-016 — Testability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must be designed to support automated and manual testing across its application modules and services.

Testing must help identify defects and prevent regressions during development and releases.

**Acceptance Criteria:**

- AC-001: Backend modules support unit and integration testing.
- AC-002: APIs support automated contract and integration testing.
- AC-003: Critical media-processing operations have automated tests.
- AC-004: Database migrations are validated through testing.
- AC-005: Critical user workflows are covered by end-to-end tests.
- AC-006: The project provides documented testing instructions.

### 5.7 Compatibility and Portability

#### NFR-017 — Platform Compatibility

**Status:** Approved

**Priority:** Must have

**Description:**

The system must operate across supported self-hosted deployment environments and supported Android devices.

Application components must maintain compatibility with their documented runtime and platform requirements.

**Acceptance Criteria:**

- AC-001: The backend supports its documented Java runtime baseline.
- AC-002: The Android application supports its documented minimum Android version.
- AC-003: The system supports deployment on supported Linux-based server environments.
- AC-004: The deployment supports compatible Docker and Docker Compose versions.
- AC-005: Supported platform versions and compatibility requirements are documented.

#### NFR-018 — Storage Portability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must maintain a provider-independent storage architecture to reduce dependency on a specific S3-compatible
storage implementation.

Storage operations must remain consistent across supported providers.

**Acceptance Criteria:**

- AC-001: Application-level media operations use the storage abstraction.
- AC-002: The system supports MinIO and compatible S3 storage providers.
- AC-003: Storage provider-specific implementation details are isolated from application modules.
- AC-004: Changing storage providers must not require changes to application-level media management functionality.
- AC-005: Storage compatibility requirements are documented.

### 5.8 Backup and Disaster Recovery

#### NFR-019 — Backup Reliability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must support reliable backup operations to protect application data and media against accidental deletion,
infrastructure failures, and data corruption.

Backups must include the information required to restore a Voyagrr installation.

**Acceptance Criteria:**

- AC-001: Backups include PostgreSQL database data.
- AC-002: Backups include original media and generated media files.
- AC-003: Backup operations must preserve data consistency.
- AC-004: The system provides backup completion and failure information.
- AC-005: Backup files must be protected against unauthorized access.
- AC-006: Backup and restoration procedures must be documented.

#### NFR-020 — Disaster Recovery

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide documented recovery procedures to restore Voyagrr after major failures, infrastructure loss, or
data corruption.

Recovery procedures must allow administrators to restore application data, media files, and required configurations.

**Acceptance Criteria:**

- AC-001: The project provides documented disaster recovery procedures.
- AC-002: Administrators can restore database data from available backups.
- AC-003: Administrators can restore media files from available backups.
- AC-004: Recovery procedures include required configuration and deployment information.
- AC-005: Recovery procedures are tested periodically.
- AC-006: The system provides sufficient information to identify incomplete or unsuccessful recovery operations.

### 5.9 Resource Efficiency and Observability

#### NFR-021 — Resource Utilization

**Status:** Approved

**Priority:** Must have

**Description:**

The system must use server resources efficiently to support self-hosted deployments on different VPS and server
configurations.

Media processing and background workloads must be managed according to available system resources.

**Acceptance Criteria:**

- AC-001: The system monitors CPU and memory usage.
- AC-002: Background workers operate within available resource limits.
- AC-003: Media processing must not unnecessarily exhaust available system resources.
- AC-004: The system supports configurable worker concurrency.
- AC-005: Resource requirements and recommended deployment profiles are documented.
- AC-006: Temporary files and processing artifacts are cleaned up to avoid unnecessary disk usage.

#### NFR-022 — Logging and Observability

**Status:** Approved

**Priority:** Must have

**Description:**

The system must provide consistent logging and operational visibility to support troubleshooting, monitoring, and
maintenance.

Logs and health information must help administrators identify application errors and service failures.

**Acceptance Criteria:**

- AC-001: Application services provide structured logs.
- AC-002: Logs include timestamps and severity levels.
- AC-003: The system exposes health information for required services.
- AC-004: Important application and background processing failures are logged.
- AC-005: Logs must not expose passwords, authentication tokens, or storage credentials.
- AC-006: The system supports log rotation and configurable retention.
- AC-007: Operational health information is accessible to authorized administrators.

## 6. Technical Constraints and Assumptions

### 6.1 Technology Constraints

- The backend must be developed using Java and Spring Boot as a modular monolith.
- Media processing must be handled by a separate Python worker.
- The Android application must be developed using Flutter.
- PostgreSQL must be used as the primary database and durable background job queue.
- Liquibase must manage database schema migrations.
- Redis, Kafka, and RabbitMQ are excluded from the MVP.
- The system must expose REST APIs documented using OpenAPI.

### 6.2 Infrastructure Constraints

- Voyagrr must support self-hosted deployment on a VPS or user-managed server.
- Docker Compose must be the primary deployment method for the MVP.
- The system must support configurable CPU, memory, and worker resources.
- The initial target is 1–5 users and up to 100,000 media files per installation.

### 6.3 Storage Constraints

- Media files and generated assets must be stored using MinIO or another S3-compatible storage provider.
- Only one storage provider can be configured per installation.
- Local filesystem storage is not supported as a media storage backend in the MVP.
- The storage implementation must use a provider-independent abstraction to support compatible providers.

### 6.4 Deployment Constraints

- The application must be deployable and manageable through Docker Compose.
- Persistent data must survive container restarts and application updates.
- Application configuration and secrets must be supplied through secure configuration mechanisms.
- Deployment, backup, restore, and upgrade procedures must be documented.

### 6.5 External Dependencies and Assumptions

- Users are responsible for providing and maintaining their own server infrastructure.
- Users must provide a working S3-compatible storage service.
- Reliable network connectivity is required for uploads, downloads, and communication between services.
- Email delivery infrastructure is required for account verification and password reset functionality.
- FFmpeg and other required media-processing tools must be available to the processing environment.
- The application depends on supported third-party libraries and tools, which must be maintained and updated as needed.

## 7. Open Questions, Risks and Dependencies

### 7.1 Open Questions

The following items require further clarification during technical design or implementation:

- What are the final supported image and video formats?
- What are the recommended minimum and maximum VPS resource configurations?
- What are the final performance targets for large uploads, library browsing, and video playback?
- What backup retention policy and recovery targets (RTO and RPO) should be supported?
- Which email delivery approach should be recommended for self-hosted installations?
- What are the final Android version and device compatibility requirements?

### 7.2 Technical and Operational Risks

- Limited VPS resources may affect media processing and video transcoding performance.
- Large media libraries may increase database, storage, and backup requirements.
- S3-compatible providers may differ in supported features and behavior.
- Network interruptions may affect uploads, downloads, and media processing workflows.
- Incorrect backup or restore procedures may result in data loss.
- Security vulnerabilities in application dependencies or deployment configuration may expose user data.
- Media processing failures or unsupported formats may affect the availability of previews and playback.

### 7.3 Dependencies

- A working PostgreSQL database is required for application data and background job management.
- MinIO or another supported S3-compatible provider is required for media storage.
- FFmpeg and relevant media libraries are required for video processing and playback preparation.
- Email delivery infrastructure is required for account verification and password reset.
- Docker and Docker Compose are required for the recommended MVP deployment.
- Flutter-compatible Android development and build tooling is required for the mobile application.

### 7.4 Risk Management

- Document minimum and recommended infrastructure configurations.
- Validate storage provider compatibility before supporting a provider.
- Use resumable uploads and retry mechanisms to handle network interruptions.
- Provide documented backup and restore procedures.
- Keep application dependencies updated and review security vulnerabilities.
- Ensure original media remains available when optional processing tasks fail.