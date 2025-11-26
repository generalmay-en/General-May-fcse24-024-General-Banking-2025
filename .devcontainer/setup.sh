#!/bin/bash

set -e

echo "🎯 Setting up JavaFX Banking App Environment..."

# Update system
echo "📦 Updating system packages..."
sudo apt-get update

# Install desktop environment and VNC
echo "🖥️  Installing Xfce desktop and VNC server..."
sudo apt-get install -y xfce4 xfce4-goodies tightvncserver novnc websockify

# Install JavaFX dependencies
echo "📦 Installing JavaFX graphics dependencies..."
sudo apt-get install -y libgl1-mesa-dev libglu1-mesa-dev libgtk-3-dev

# Create VNC directory
mkdir -p ~/.vnc

# Create VNC startup script
cat > ~/.vnc/xstartup << 'EOF'
#!/bin/bash
xrdb $HOME/.Xresources
startxfce4 &
EOF

chmod +x ~/.vnc/xstartup

# Set VNC password
echo "🔐 Setting VNC password to 'password'..."
echo "password" | vncpasswd -f > ~/.vnc/passwd
chmod 600 ~/.vnc/passwd

# Create application startup script
sudo tee /usr/local/bin/start-banking-app > /dev/null << 'EOF'
#!/bin/bash
echo "🚀 Starting Banking Application..."

# Kill existing VNC sessions
vncserver -kill :1 2>/dev/null || true

# Start VNC server
echo "🖥️  Starting VNC server..."
vncserver :1 -geometry 1280x800 -depth 24 -localhost no

# Start noVNC
echo "🌐 Starting noVNC web interface..."
/usr/share/novnc/utils/novnc_proxy --vnc localhost:5901 --listen 6080 &

# Wait for VNC to initialize
sleep 3

# Set DISPLAY for JavaFX
export DISPLAY=:1

# Navigate to workspace
WORKSPACE_DIR="/workspaces/$(basename $(pwd))"
cd "$WORKSPACE_DIR" || cd /workspaces/*

# Build the application
echo "🔨 Building Java application..."
mkdir -p target/classes

# Compile all Java files
find src -name "*.java" > sources.txt
javac -d target/classes @sources.txt 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Build successful! Starting application..."
    java -cp target/classes com.banking.BankingApplication
else
    echo "❌ Build failed! Check for compilation errors."
    exit 1
fi
EOF

sudo chmod +x /usr/local/bin/start-banking-app

echo ""
echo "✅ Setup complete!"
echo ""
echo "📋 NEXT STEPS:"
echo "   1. Run: start-banking-app"
echo "   2. Go to 'Ports' tab in VS Code"
echo "   3. Click globe icon 🌐 next to port 6080"
echo "   4. Password: 'password'"
echo ""
