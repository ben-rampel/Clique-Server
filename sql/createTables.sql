CREATE TABLE IF NOT EXISTS PersonGroup
(
    Id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    MemberTurnover INTEGER         NOT NULL,
    Longitude      DECIMAL(30, 10),
    Latitude       DECIMAL(30, 10),
    PRIMARY KEY (`Id`)
);

CREATE TABLE IF NOT EXISTS PendingMember
(
    PersonId BIGINT UNSIGNED,
    GroupId  BIGINT UNSIGNED,
    CONSTRAINT GROUP_PENDINGMEMBER FOREIGN KEY (GroupId) REFERENCES `PersonGroup` (Id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Person
(
    Id          BIGINT UNSIGNED    NOT NULL AUTO_INCREMENT,
    Username    VARCHAR(50) UNIQUE NOT NULL,
    FirstName   VARCHAR(50),
    LastName    VARCHAR(50),
    Password    VARCHAR(100)       NOT NULL,
    Bio         VARCHAR(2000),
    PhoneNumber VARCHAR(20) UNIQUE,
    GroupId     BIGINT UNSIGNED,
    PRIMARY KEY (Id),
    CONSTRAINT GROUP_PERSON FOREIGN KEY (GroupId) REFERENCES `PersonGroup` (Id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS PersonInterest
(
    PersonId BIGINT UNSIGNED,
    Interest VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Message
(
    Id       BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    Content  VARCHAR(2000)   NOT NULL,
    Date     TIMESTAMP       NOT NULL,
    PersonId BIGINT UNSIGNED,
    PRIMARY KEY (Id),
    CONSTRAINT PERSON_MESSAGE FOREIGN KEY (PersonId) REFERENCES `Person` (Id) ON DELETE CASCADE
)