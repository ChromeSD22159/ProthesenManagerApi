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

Thank you for using my Ktor project! If you have any questions or need assistance, feel free to reach out.