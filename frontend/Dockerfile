# pull official base image
FROM node:alpine

# set working directory in the container
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY ./app ./
RUN npm install
RUN npm run build

WORKDIR /server
ENV PATH /server/node_modules/.bin:$PATH
COPY ./server ./
RUN npm install
# start server
CMD ["npm", "run", "server"]
