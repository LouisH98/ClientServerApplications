using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Server
{
    class Player
    {
        public bool hasBall
        {
            get; set;
        }

        public int ID {
            get;
            private set;
        }
       
        public StreamWriter writer { get; private set; }
        public StreamReader reader { get; private set; }

        public Player(int id, bool hasBall, StreamWriter writer, StreamReader reader)
        {
            ID = id;
            this.writer = writer;
            this.reader = reader;
            this.hasBall = hasBall;
        }


        public override bool Equals(Object obj)
        {
            return ID == ((Player)obj).ID;
        }

        public override int GetHashCode()
        {
            return ID.GetHashCode();
        }
    }
}
