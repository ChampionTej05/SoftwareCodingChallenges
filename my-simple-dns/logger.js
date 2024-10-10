const winston = require('winston');

const logger = winston.createLogger({
    level: 'debug',
    format: winston.format.combine(
      winston.format.timestamp(),  // Generate the timestamp
      winston.format.printf(({ level, message, timestamp }) => {
          return `${timestamp} [${level}] ${message}`; // Include timestamp in the formatted string
      })
    ),
    transports: [
      new winston.transports.File({ filename: 'error.log', level: 'error' }),
      new winston.transports.File({ filename: 'combined.log' }),
      new winston.transports.Console()
    ]
  });

logger.info("logger setup")

module.exports = logger;
