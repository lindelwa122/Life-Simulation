# Life Simulation - Docker Guide

## Building the Docker Image

```bash
docker build -t life-simulation:latest .
```

## Running with Default Config

```bash
docker run -it life-simulation:latest
```

### Using X11 Display (Linux/Mac)

To see the GUI window, you need to forward the X11 display from your host:

**Linux:**
```bash
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
  life-simulation:latest
```

**Mac (using XQuartz):**
```bash
# First, allow connections from docker in XQuartz
xhost + 127.0.0.1

docker run -it \
  -e DISPLAY=docker.for.mac.host.internal:0 \
  life-simulation:latest
```

## Running with Custom Config

Create your own `simulation-config.json` and mount it to the container:

**With X11 forwarding (Linux):**
```bash
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
  -v /path/to/your/config:/app/config \
  life-simulation:latest
```

**Without display (runs in background):**
```bash
docker run -it -v /path/to/your/config:/app/config life-simulation:latest
```

Replace `/path/to/your/config` with the directory containing your `simulation-config.json` file.

### Example

```bash
# Place your config in ./my-config/simulation-config.json
docker run -it \
  -e DISPLAY=$DISPLAY \
  -v /tmp/.X11-unix:/tmp/.X11-unix:rw \
  -v ./my-config:/app/config \
  life-simulation:latest
```

## Publishing to Docker Registry

### Docker Hub

```bash
# Tag the image
docker tag life-simulation:latest <your-username>/life-simulation:latest

# Push to Docker Hub
docker push <your-username>/life-simulation:latest
```

### Other Registries

```bash
# Tag for your registry
docker tag life-simulation:latest registry.example.com/life-simulation:latest

# Push to registry
docker push registry.example.com/life-simulation:latest
```

## Config File Format

The application looks for `simulation-config.json` in the working directory. If you mount a custom config, place it in the mounted volume with the filename `simulation-config.json`.

See the default `simulation-config.json` in the repository for configuration options.
