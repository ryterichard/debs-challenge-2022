# pull official base image
FROM node:alpine

# set working directory in the container
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH

# add doge-traders/src, package*.json, etc.
COPY . ./
RUN npm install

# start app
CMD ["npm", "run", "start"]