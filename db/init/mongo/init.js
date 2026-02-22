const db = db.getSiblingDB('quizapp');
const data = JSON.parse(cat('/docker-entrypoint-initdb.d/fut.json'));
// Store the dataset as a single document for easy retrieval.
db.fut_questions.insertOne(data);