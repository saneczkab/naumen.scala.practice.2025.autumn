-- !Ups

CREATE TABLE job (
    hh_id BIGINT NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    requirement TEXT,
    responsibility TEXT,
    salary_from INT,
    salary_to INT,
    salary_currency VARCHAR(3),
    salary_gross BOOLEAN,
    url TEXT NOT NULL,
    area_id BIGINT NOT NULL
);

CREATE TABLE keyword (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE job_keyword (
    job_id BIGINT NOT NULL REFERENCES job(hh_id) ON DELETE CASCADE,
    keyword_id BIGINT NOT NULL REFERENCES keyword(id) ON DELETE CASCADE,
    PRIMARY KEY (job_id, keyword_id)
);

CREATE TABLE job_area (
    job_id BIGINT NOT NULL REFERENCES job(hh_id) ON DELETE CASCADE,
    area_id BIGINT NOT NULL,
    PRIMARY KEY (job_id, area_id)
);

-- !Downs

DROP TABLE IF EXISTS job_keyword;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS keyword;
DROP TABLE IF EXISTS job_area;
