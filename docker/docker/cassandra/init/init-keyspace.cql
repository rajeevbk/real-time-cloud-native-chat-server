CREATE KEYSPACE IF NOT EXISTS chat_server
WITH replication = {
  'class': 'SimpleStrategy',
  'replication_factor': 1
};
