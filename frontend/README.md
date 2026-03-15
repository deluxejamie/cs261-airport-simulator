# Frontend Service

## Running the frontend service

To run the server locally, firstly create a `.env` file within the frontend folder. Then add the following env variable:

```bash
NEXT_PUBLIC_API_BASE_URL="http://localhost:8080" # Adjust if hosted elsewhere
```

Then run the development server:

```bash
npm run dev
```

Assuming no other environment variables have been set, the next application should now be accessible at [http://localhost:3000](http://localhost:3000).
