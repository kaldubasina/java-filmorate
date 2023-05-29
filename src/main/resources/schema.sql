CREATE TABLE IF NOT EXISTS users (
	id int AUTO_INCREMENT PRIMARY KEY,
	email varchar UNIQUE NOT NULL,
	login varchar UNIQUE NOT NULL,
	name varchar NOT NULL,
	birthday date NOT NULL CHECK(birthday <= CAST(now() as date))
);

CREATE TABLE IF NOT EXISTS user_friends (
  user_id int REFERENCES users(id) NOT NULL,
  friend_id int REFERENCES users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa (
  id int AUTO_INCREMENT PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
  id int AUTO_INCREMENT PRIMARY KEY,
  name varchar NOT NULL,
  description varchar(200) NOT NULL,
  date date NOT NULL,
  duration int NOT NULL CHECK(duration > 0),
  rate int CHECK(rate >= 0),
  mpa_id int REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS film_likes (
  film_id int REFERENCES films(id) NOT NULL,
  user_id int REFERENCES users(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
  id int AUTO_INCREMENT PRIMARY KEY,
  name varchar UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id int REFERENCES films(id) NOT NULL,
  genre_id int REFERENCES genres(id) NOT NULL
);