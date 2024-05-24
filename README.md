# Welcome to ProthesenManager Api!

This project is based on Ktor, a lightweight framework for building web applications in Kotlin.

## Setup Instructions:

1. **Create Required Files**:
    - Create a `version.txt` file in the root directory.
    - Create a `.env` file with the following parameters:
      ```
      DB_PORT=<Port>
      DB_NAME=<Database Name>
      DB_HOST=<Database Host>
      DB_USERNAME=<Database Username>
      DB_PASSWORD=<Database Password>
      
      MAIL_USER_NAME = <E-Mail adress>
      MAIL_USER_PASSWORD = <E-Mail Password>
      MAIL_HOST = <SMTP Server Host>
      MAIL_PORT = <Port>
      ```

2. **Install Required Tools**:
    - Ensure every developer has Postman, Mamp, and TablePlus installed.

3. **Postman Test Routes**:
    - Import the test routes from the file "ProthesenManagerApi.postman_collection.json" located in the root directory.
    - Two environments are provided:
        - **KTOR LOCAL**:
            - Variable Name: `base_url`
            - Initial Value: `http://localhost:8080` (Customize to your local URL)
            - Current Value: `http://localhost:8080`
        - **KTOR ProthesenApi Live**:
            - Variable Name: `base_url`
            - Initial Value: [Contact frederik.kohler for Server Staging DB URL](mailto:info@frederikkohler.de)
            - Current Value: [Contact frederik.kohler for Server Staging DB URL](mailto:info@frederikkohler.de)

## Deployment
### Server Services:

The ProthesenManager API is managed as a service with the following configuration:
```
[Unit]
Description=prothesenmanager.service
After=network.target
StartLimitIntervalSec=10
StartLimitBurst=5

[Service]
Type=simple
Restart=always
RestartSec=1
User=root
EnvironmentFile=/etc/environment
ExecStart=/usr/lib/jvm/default-java/bin/java  -jar /var/www/vhosts/<domain>/<sub.domain>/fat.jar
```

#### Start Service:
- This command starts the ProthesenManager service, allowing the ProthesenManager API to run.
#### Check Service Status:
- This command checks the status of the ProthesenManager service, indicating whether it is running or not.
#### Stop Service:
- This command stops the ProthesenManager service, halting the execution of the ProthesenManager API.
#### Enable Service at Boot:
- This command enables the ProthesenManager service to start automatically when the system boots up.

### [Ktor deploy steps](https://gist.github.com/philipplackner/bbb3581502b77edfd2b71b7e3f7b18bd)


Thank you for using my Ktor project! If you have any questions or need assistance, feel free to reach out.