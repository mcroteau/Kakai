create table if not exists users (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying (253) NOT NULL,
	phone character varying(40) NOT NULL,
	username character varying(253),
	password character varying(155) NOT NULL,
	guid character varying(155),
	date_created bigint default 0
);

create table if not exists roles (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(155) NOT NULL UNIQUE
);

create table if not exists user_permissions(
	user_id bigint REFERENCES users(id),
	permission character varying(55)
);

create table if not exists user_roles(
	role_id bigint NOT NULL REFERENCES roles(id),
	user_id bigint NOT NULL REFERENCES users(id)
);

create table if not exists statuses (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(131) NOT NULL,
	constraint status_name unique(name)
);

create table if not exists prospects (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(254) NOT NULL,
	email character varying(254),
	phone character varying(142),
	contacts text,
	notes text,
	constraint prospect_name unique(name),
	status_id bigint NOT NULL REFERENCES statuses(id)
);

create table if not exists activities (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	name character varying(254) NOT NULL,
	constraint activity_name unique(name)
);

create table if not exists efforts (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	start_date bigint default 0,
	end_date bigint,
	finished boolean default false,
	success boolean default false,
	starting_status_id bigint NOT NULL REFERENCES statuses(id),
	ending_status_id bigint REFERENCES statuses(id),
	prospect_id bigint NOT NULL REFERENCES prospects(id)
);

create table if not exists prospect_activities (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	task_date bigint default 0,
	complete_date bigint default 0,
	completed boolean default false,
	five_reminder boolean default false,
	fifteen_reminder boolean default false,
	thirty_reminder boolean default false,
	notified_five boolean default false,
	notified_fifteen boolean default false,
	notified_thirty boolean default false,
	phones text default '',
	time_zone character varying(151),
	effort_id bigint REFERENCES efforts(id),
	activity_id bigint NOT NULL REFERENCES activities(id),
	prospect_id bigint NOT NULL REFERENCES prospects(id),
	completed_by_user_id bigint REFERENCES users(id)
);



