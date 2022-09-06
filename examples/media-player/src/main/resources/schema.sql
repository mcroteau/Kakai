create table media (
	id bigint PRIMARY KEY AUTO_INCREMENT,
	uri character varying(254) NOT NULL,
	title character varying(254) NOT NULL,
	artist character varying(254) NOT NULL
);