##Enhancements before Deployment
Before deploying the code into production i would make the following changes

1. **Database usage:** I would use a Database like MySQL instead of hashmap for storing event values ensuring values are not erased on startup.


2. **Configuring Cron period/retries:** In production I would configure the Cron period which is currently 10 seconds in a config/yml file instead of hardcoding it.


3. **External API usage:** Similar to the above i word configure the external API endpoint in a config/yml file instead of hardcoding it